log4j 1.2.x reloader
==============
log4j 1.2.x utility 
                  
 [![java/maven build](https://github.com/silviuilie/reload-log4j/actions/workflows/maven.yml/badge.svg)](https://github.com/silviuilie/reload-log4j/actions/workflows/maven.yml)
 [![Coverage Status](https://coveralls.io/repos/silviuilie/reload-log4j/badge.png?branch=master)](https://coveralls.io/r/silviuilie/reload-log4j?branch=master) [![Dependency Status](https://www.versioneye.com/user/projects/54436bde53acfab90700001c/badge.svg?style=flat)](https://www.versioneye.com/user/projects/54436bde53acfab90700001c)

 

 [![Build Status](https://travis-ci.org/silviuilie/reload-log4j.svg?branch=master)](https://travis-ci.org/silviuilie/reload-log4j)
 
why  
=

runtime change Log4j 1.2.x level/priority using a simple UI 
 

use it as a Servlet
=

in the deployment descriptor of your application the following context parameters must be used to configure the servlet :

- log4j-base-packageName  package to be scanned; **mandatory parameter**.
- log4j-authorization-class class name of the `Log4jApplicationContext.Log4jUtilityAuthorization` implementation.
**optional parameter**, if not defined `eu.pm.tools.log4j.Log4jApplicationContext.DefaultLog4jUtilityAuthorization` will be used.
- log4j-jsp-location, location of the JSP files. **optional parameter** , if not defined `/WEB-INF/jsp` will be used.

(?)
probably the Servlet, rather than Controller configuration should be used since it is easier to configure.

the utility is available by default at `<context>/reload.log4j`. see `src/main/resources/META-INF/web-fragment.xml` for details.

use it as Spring Controller
=

  create a bean for Log4jApplicationContext to describe the *host* application :

* package to be scanned and
* authorization to be performed on the actions.

add a `Log4jApplicationContext` spring bean in your spring context

     @Bean
     public Log4jApplicationContext log4jApplicationContext() {
         return new Log4jApplicationContext("<your app package>", Log4jApplicationContext.DEFAULT_AUTHORIZATION);
     }

The default authorization ( `Log4jApplicationContext.DEFAULT_AUTHORIZATION` ) allows access to log4j level reload
utilities to *all requests*. To restrict access, create your own authorization by implementing `Log4jUtilityAuthorization` :


    @Bean
    public CustomAuthorization implements Log4jUtilityAuthorization {
        @Override
        public boolean authorize(HttpSession session) {
            // auth. code ..
        }
    }

and initialize `Log4jApplicationContext` using it.

(?) This is needed to search/scan only classes in specified package and to limit the users that can access to tool.

### configure spring component scanner to include `eu.pm.tools.log4j` package


    @ComponentScan(
            basePackages = {
                    // your packages
                    "your.packages",
                    "eu.pm.tools.log4j"
            }
    )

(?) This is needed to enable @Controller class that manages the utility requests.


### web.xml configuration

Add the the *.log4j url extension to your DispatcherServlet :

    <servlet-mapping>
        <servlet-name>your-spring-servlet-dispatcher-name</servlet-name>
        <url-pattern>*.log4j</url-pattern>
    </servlet-mapping>

(?) This is needed to enable your DispatcherServlet to serve the utility requests :

- `reload.log4j` is the "home" of the utility, loads the UI,
- `log4jFindClass.log4j` used to search classes,
- `log4jReload.log4j` used to reload level for a specified class.

### JSP configuration

If the host application JSPs are not located in the (more or less) standard location `/WEB-INF/jsp/`, add a new
view resolver in your spring configuration with the `/WEB-INF/jsp/` prefix.
Otherwise, no other configuration is required.


similar projects
=

[https://github.com/mrsarm/log4jwebtracker](https://github.com/mrsarm/log4jwebtracker) (Java web tool to setup at runtime the log level of Log4j appenders in an application, and read the log at runtime)
