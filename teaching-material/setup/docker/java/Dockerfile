FROM maven:3.8.2-openjdk-11

ARG code_server_version=3.11.1

RUN apt-get update
RUN apt-get install -y openssh-server
RUN apt-get install -y less
RUN mkdir -p ~/.local/lib ~/.local/bin
RUN wget https://github.com/cdr/code-server/releases/download/v${code_server_version}/code-server-${code_server_version}-linux-amd64.tar.gz 
RUN tar -C ~/.local/lib -xzf code-server-${code_server_version}-linux-amd64.tar.gz
RUN mv ~/.local/lib/code-server-${code_server_version}-linux-amd64 ~/.local/lib/code-server-${code_server_version}
RUN ln -s ~/.local/lib/code-server-${code_server_version}/bin/code-server /usr/bin

EXPOSE 8080

CMD code-server --bind-addr 0.0.0.0:8080 --cert