log4j-reloader
==============

log4j utility


why to use
---

if you need to change Log4j level/priority using a simple UI, while the your webapp is running


how to use
===

1. as a Servlet
---

in the deployment descriptor of your application the following context parameters must be used to configure the servlet :

- log4j-base-packageName  package to be scanned; **mandatory parameter**.
- log4j-authorization-class class name of the `Log4jApplicationContext.Log4jUtilityAuthorization` implementation.
**optional parameter**, if not defined `eu.pm.tools.log4j.Log4jApplicationContext.DefaultLog4jUtilityAuthorization` will be used.
- log4j-jsp-location, location of the JSP files. **optional parameter** , if not defined `/WEB-INF/jsp` will be used.

(?)
probably the Servlet, rather than Controller configuration should be used since it is easier to configure.

the utility is available by default at `<context>/reload.log4j`. see `src/main/resources/META-INF/web-fragment.xml` for details.

2. as a spring Controller
---
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

