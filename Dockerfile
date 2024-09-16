# Usar uma imagem base do Java 17
FROM eclipse-temurin:17-jdk-alpine

# Criar diretório de trabalho
WORKDIR /app

# Copiar o arquivo JAR para o contêiner
COPY build/libs/*.jar app.jar

# Expor a porta 8080
EXPOSE 8080

# Definir a variável de ambiente para a porta
ENV PORT 8080

# Comando para iniciar a aplicação
ENTRYPOINT ["java","-jar","app.jar"]
