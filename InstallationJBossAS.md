

---


# Configure Server #

Before you deploy PSI Probe to JBoss, you must configure the server. This may require you to restart the service.

## Security ##

Because of its elevated permissions, you should not allow anonymous access to PSI Probe.

### Create Users ###

Create a file `$JBOSS_SERVER_HOME/conf/props/probe-users.properties` which lists each user and its password in the following format:

```
admin=t0psecret
```

### Assign Roles ###
Create a file `$JBOSS_SERVER_HOME/conf/props/probe-roles.properties` which lists each user and its roles in the following format:

```
admin=manager
```

PSI Probe uses four security roles (in order of increasing [privileges](Features#Features_by_Role.md)):

  * **probeuser**
  * **poweruser**
  * **poweruserplus**
  * **manager** - This is the same role required by Tomcat Manager and has the highest level of privileges.

**Note:** On JBoss 6 and above, you can use `manager-gui` instead of `manager`.

### Declare Application Policy ###

Edit `$JBOSS_SERVER_HOME/conf/login.xml` and add the following code to the `<policy>` element:

```
<application-policy name="probe">
  <authentication>
    <login-module code="org.jboss.security.auth.spi.UsersRolesLoginModule" flag="required">
      <module-option name="usersProperties">props/probe-users.properties</module-option>
      <module-option name="rolesProperties">props/probe-roles.properties</module-option>
    </login-module>
  </authentication>
</application-policy>
```

## Ensure a privileged context ##

PSI Probe needs elevated permissions in JBoss to successfully deploy and undeploy applications.

Add the following attribute to `$JBOSS_SERVER_HOME/deploy/jbossweb-tomcat55.sar/META-INF/jboss-service.xml`:

```
<attribute name="AllowSelfPrivilegedWebApps">true</attribute>
```

# Modify probe.war #

You may need to edit or remove files in the probe.war to get it to work correctly.  To do so, rename it to probe.zip and open it with your favorite ZIP program.  When you're finished with the edits, rename it back to probe.war.

## JMX ##

JBoss has its own JMX library which may conflict with the one included with PSI Probe.  Remove `/WEB-INF/lib/jmxri-1.2.1.jar`.

## Log4j ##

JBoss has a dedicated directory for its logs.  Open `/WEB-INF/classes/log4j.properties` in a text editor.  Find the line that reads:
```
log4j.appender.R.File=${catalina.base}/logs/probe.log
```

and change it to:
```
log4j.appender.R.File=${jboss.server.log.dir}/probe.log
```

# Deployment #

  1. Copy probe.war to `$JBOSS_SERVER_HOME/deploy/`
  1. Restart JBoss.

# Testing #

Once you have deployed PSI Probe to JBoss, verify that you can access it in your web browser by entering PSI Probe's URL (e.g. `http://localhost:8080/probe`).

When you are prompted for a username and password, enter the credentials for the manager account you created earlier.

If you have any problems, see our [Troubleshooting](Troubleshooting.md) page.

## Known Issues ##

  * JBoss 3.2.8SP1 has a [bug](https://jira.jboss.org/jira/browse/JBAS-3006) which does not allow Probe to restart applications.  It has been fixed in a later version.