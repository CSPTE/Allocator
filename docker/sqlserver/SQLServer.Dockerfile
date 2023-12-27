FROM mcr.microsoft.com/mssql/server:2017-latest

# Create a config directory
RUN mkdir -p /usr/config
WORKDIR /usr/config

# Bundle config source
COPY . /usr/config

# Grant permissions for to our scripts to be executable
RUN chmod +x /usr/config/entrypoint.sh
RUN chmod +x /usr/config/configure-db.sh

RUN apt-get update \
    && apt-get install dos2unix -y

RUN dos2unix /usr/config/entrypoint.sh \
    && dos2unix /usr/config/configure-db.sh \
    && dos2unix /usr/config/setup.sql

ENTRYPOINT ["./entrypoint.sh"]