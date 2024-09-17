# Usar uma imagem base do Java 17
FROM eclipse-temurin:17-jdk-alpine

# Configurar o diretório de trabalho
WORKDIR /app

# Copiar todos os arquivos do projeto para o contêiner
COPY . .

# Conceder permissão de execução para o script gradlew
RUN chmod +x ./gradlew

# Executar o build do projeto
RUN ./gradlew build

# Copiar o arquivo JAR gerado para o contêiner
COPY build/libs/*.jar /app/app.jar

# Expor a porta 8080
EXPOSE 8080

# Definir o comando de entrada
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
