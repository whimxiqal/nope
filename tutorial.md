# Tutorial

Now that you have installed the plugin, you can type ``/nope`` command to ensure that the plugin is functional. To see Nope's command help, do ``/nope help``.

> If a command signature has ``[-z <zone name>]``, it implies that Nope can infer the Zone by itself if you stand inside it.

## Creating Your First Zone

**Example:** Creating A Zone.

Our first step is to get the Nope Zone Wand, which you can get with typing ``/nope wand`` in the console. Left-clicking and right-clicking stores your first and second positions for a selection respectively. The plugin uses the positions in your selection to create Zones. To actually create a Zone you need to do ``/nope create <zone-name>``. 

Now that you have created the zone, you can see its data with ``/nope info [-z <zone-name>]``. You can see the Zone's boundaries with ``/nope show [-z <zone-name>]``. As you can see, there's not too much going on, so let's change that.

> You can always remove a zone with ``/nope remove <zone-name>``.

## Disabling Block Breakage

**Example:** Denying Block Breakage. 

To do this we need to set the corresponding setting to ``deny``. A majority of Nope's functionality is concentrated in the settings. You can view them with doing ``/nope settings``. Now we can see that the setting ``block-break`` is what we are looking for. In order to disable it you can do ``/nope set [-z <zone-name>] block-break deny``. 

When you will try to break blocks in your Zone, you will find out that you can't do it and that we successfully disabled block breakage.

> You can do the exact same thing with many other things. For example, you can deny PVP with denying ``pvp``, or disable player damage altogether with setting the ``invincible-players`` setting to ``true``. If you want to remove data about a setting in a Zone, then you can do ``/nope unset [-z <zone-name>] <setting>``. In case you want to wipe all settings in a Zone, you can do ``/nope clear [-z <zone-name>]``

## A Bit about The Host Tree

While doing ``/nope info`` outside of your Zone may notice that there are also Zones that span the entire world. You can set setting values to them just like with normal Zones. They're called WorldHosts. And what about one step further? Does Nope have a Zone that spans the entire server? Correct, and it's called the GlobalHost (``_global``). Zones, WorldHosts and the GlobalHost as a whole form the HostTree. It's divided into three tiers:

 1. GlobalHost
 2. WorldHosts
 3. Zones

Hosts with higher tier numbers override the settings of Hosts with lower tier numbers. For example: say that we have a Zone, where block breaking is allowed, and we have disabled block breaking in the GlobalHost. As a result, we can break blocks in the Zone, but we can't break them outside of it. This is because our Zone overrides the settings of the GlobalHost.

## On Priorities

Priorities are a natural progression from the HostTree tiers. They allow for finer control of setting overriding in the scale of worlds. As before, Hosts with higher priorities override the settings of those with lower priorities if they intersect.


**Example:** We have a Zone called ``castle`` with disabled block placement and breakage, and we have made another Zone inside of it called ``sandbox``, which we want to be a free-build area.

Without priorities, if we would have disabled block breakage and block placement in our castle, then our sandbox would be affected by those changes too. But if we have set ``sandbox``'s priority to be larger than the priority of ``castle``, then we can explicitly allow block breakage and placement in ``sandbox``. 

As a result, we can break and place blocks in our sandbox, even though these actions are disabled in the castle as a whole.

> You can explicitly set a priority to a Zone with ``/nope setpriority [-z <zone name>] <priority>``, where ``priority`` is a non-negative number. Priorities -1 and -2 are reserved for WorldHosts and the GlobalHost respectively. You can also explicitly set a priority for a Zone when creating one by appending ``-p priority`` to the command, where ``priority`` is our given priority.

## Targetting

Our final section is about Nope's targetting system. It's useful when you want some users to be an exception from a Zone's setting.

**Example:** making an user to be an exception to a setting

Let's begin by setting a targetting type for a setting. We can do it with ``/nope target type [-z <zone-name>] <setting> <blacklist|whitelist>``. In our case the setting will be ``block-break``, and we will have a whitelist. To add users to our new whitelist, we can do ``/nope target add player <setting> <player-name>``, where the ``setting`` is exactly the same setting we assigned a whitelist to. You will see that the chosen user will be an exception from the setting.

> You can also add users with a certain permission using ``/nope target add permission <setting> <permission> <setting-value>``. You can remove users and permissions with ``/nope target remove <user|permission> <setting> <username|permission-name>``