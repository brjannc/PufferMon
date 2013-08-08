PufferMon
=====

PufferMon is a CraftBukkit plugin for monitoring Minecraft servers with icinga/nagios.

Configuration
-------------

There are only two configuration keys in config.yml:

*   *query-port*

    The port to bind to, 2565 by default.
*   *whitelist*

    The icinga/nagios servers allowed to query the plugin. Put your monitoring server's IP here.

Usage
-----

Place check_puffermon.sh in /usr/lib/nagios/plugins

Put puffermon.cfg in /etc/icinga/objects/global

Define the service in the host config file in /etc/icinga/objects/<yourhost_namehere>

```
define service {
  service_description                   puffermon
  check_command                         check_puffermon!127.0.0.1!2565
  host_name                             <yourhost_name>
  check_period                          24x7
  notification_period                   24x7
  contact_groups                        +admins
  check_interval                        1
  notification_options                  n
  event_handler_enabled                 0
  use                                   generic-service,pnp-service
}
```