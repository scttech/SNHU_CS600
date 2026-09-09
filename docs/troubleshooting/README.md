# Troubleshooting

## Error starting application

You may encounter errors such as:

`org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'entityManagerFactory' defined in class path resource [org/springframework/boot/hibernate/autoconfigure/HibernateJpaConfiguration.class]: Unable to create requested service [org.hibernate.engine.jdbc.env.spi.JdbcEnvironment] due to: Unable to determine Dialect without JDBC metadata (please set 'jakarta.persistence.jdbc.url' for common cases or 'hibernate.dialect' when a custom Dialect implementation must be provided)`

`Caused by: org.hibernate.service.spi.ServiceException: Unable to create requested service [org.hibernate.engine.jdbc.env.spi.JdbcEnvironment] due to: Unable to determine Dialect without JDBC metadata (please set 'jakarta.persistence.jdbc.url' for common cases or 'hibernate.dialect' when a custom Dialect implementation must be provided)`

`Caused by: org.hibernate.HibernateException: Unable to determine Dialect without JDBC metadata (please set 'jakarta.persistence.jdbc.url' for common cases or 'hibernate.dialect' when a custom Dialect implementation must be provided)`

### Solution

Ensure that docker has been started with

`docker-compose up -d`

## Error bring Docker up

failed to connect to the docker API at unix:///Users/chris/.docker/run/docker.sock; check if the path is correct and if the daemon is running: dial unix /Users/chris/.docker/run/docker.sock: connect: no such file or directory

### Solution

Ensure that docker is up and running (i.e. Docker Desktop is started)