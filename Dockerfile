# Usar uma imagem base do Java 17
FROM eclipse-temurin:17-jdk-alpine

# Criar diretório de trabalho
WORKDIR /app

# Copiar o arquivo JAR gerado para o contêiner
COPY build/libs/allreps-0.0.1-SNAPSHOT.jar /app


# Expor a porta 8080
EXPOSE 8080

# Definir a variável de ambiente para a porta
ENV PORT 8080

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
