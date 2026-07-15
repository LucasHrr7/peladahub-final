# PeladaHub

Aplicação Java sem Spring para organizar rodadas de futebol. O servidor entrega o site e salva o estado da rodada no MySQL.

## Rodar localmente

Defina as variáveis de ambiente e execute com Docker (não exige Maven instalado):

```powershell
$env:DB_URL='jdbc:mysql://HOST:3306/pelada_db?useSSL=true&serverTimezone=UTC'
$env:DB_USER='USUARIO'
$env:DB_PASSWORD='SENHA'
docker build -t peladahub .
docker run -p 8080:8080 --env DB_URL --env DB_USER --env DB_PASSWORD peladahub
```

Abra `http://localhost:8080`. A tabela `rodada_estado` é criada automaticamente.

## Hospedar no Render

1. Suba este diretório para um repositório GitHub privado ou público.
2. Crie um banco MySQL externo e copie sua URL de conexão JDBC.
3. No Render, crie um serviço a partir do repositório; o arquivo `render.yaml` detecta o Dockerfile.
4. Cadastre `DB_URL`, `DB_USER` e `DB_PASSWORD` nas variáveis do serviço.
5. Publique e use a URL gerada no celular.

Nunca salve senha de banco no Git ou no código.
