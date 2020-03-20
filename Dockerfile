# Pushed as kesstyle/keshtml:consulled

FROM openjdk:8

MAINTAINER "Siarhei Kavalchuk" "kess@tut.by"

EXPOSE 8080/tcp
EXPOSE 8088/tcp
EXPOSE 8443/tcp
EXPOSE 8787/tcp

ENV JAR_NAME html_aggregator-1.0.0.jar
#ENV CONSUL_HOME consul.zip

ENV APP_PROPS application.properties
ENV TARGET /html_agg_app
ENV JAVA_OPTS="-server -verbose:gc -Xms128m -Xmx256m -XX:MetaspaceSize=256m -XX:+UseConcMarkSweepGC"

RUN cd /
RUN mkdir html_agg_app

COPY $JAR_NAME $TARGET
COPY $APP_PROPS $TARGET
#COPY $CONSUL_HOME /tmp
#COPY wrapper.sh $TARGET

#RUN unzip /tmp/consul.zip
#RUN install consul /usr/bin/consul
#RUN addgroup --system consul

RUN addgroup --system kes_group && adduser --system kes && adduser kes kes_group
RUN chown kes:kes_group $TARGET
RUN chown kes:kes_group $TARGET/$JAR_NAME
RUN chown kes:kes_group $TARGET/$APP_PROPS
#RUN chown kes:kes_group $TARGET/wrapper.sh
RUN apt-get update -y && apt-get upgrade -y && apt-get install sudo -y && apt-get install nano -y

USER kes

CMD java -jar /html_agg_app/$JAR_NAME --spring.config.location=/html_agg_app/$APP_PROPS
