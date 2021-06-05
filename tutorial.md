# Tutorial

Now that you have installed the plugin, you can type `/nope` command to ensure that the plugin is functional. To see Nope's command help, do `/nope help`.

> If a command signature has `[-z <zone name>]`, it implies that Nope can infer the zone by itself if you stand inside of it.

## Creating Your First Zone - Example

Our first step is to get the Nope zone Wand, which you can get with typing `/nope wand` in the console. This wand works similarly to WorldEdit's selection wand; left-clicking and right-clicking stores your first and second positions for a selection, respectively, and Nope uses the positions in your selection to create zones. Then, create a zone with these corners by running the command `/nope create <zone-name>`. 

> You can always remove a zone with `/nope remove <zone-name>`.

Now that you have created the zone, you can see its data with `/nope info [-z <zone-name>]`. You can see the zone's boundaries with `/nope show [-z <zone-name>]`. As you can see, the zone's not doing much, so let's change that.

## Disabling Block Breakage - Example

To do this we need to set the corresponding setting to `deny`. A majority of Nope's functionality is concentrated around the settings. You can view them with doing `/nope settings`. Now we can see that the setting `block-break` is what we are looking for. In order to disable it you can do `/nope set [-z <zone-name>] block-break deny`. 

When you will try to break blocks in your zone, you will find out that you can't do it and that we successfully disabled block breakage.

> If you can still break things, it might be because you have admin privileges. When testing, ensure that you have permission `nope.unrestricted` set to `false` for your user.

> You can do the exact same thing with many other things. For example, you can deny PVP with denying `pvp`, or disable player damage altogether with setting the `invincible-players` setting to `true`. If you want to remove a setting in a zone, then you can do `/nope unset [-z <zone-name>] <setting>`. In case you want to wipe all settings in a zone, you can do `/nope clear [-z <zone-name>]`

## A Bit about The Host Tree

While doing `/nope info` outside of your zone, you may notice that there are also hosts that span the entire world. You can set setting values to them just like with zones. These are called world hosts (internally, WorldHosts). There is also a global host (internally, GlobalHost (`_global`)). Zones, world hosts, and the global host, as a whole, form the host tree.

 1. Global host
 2. World hosts
 3. Zones

## On Priorities

> The GlobalHost has priority `-2`, all world hosts have priority `-1`, and zones may only have priorities greater than `0`. 

Priorities allow for finer control of how conflicting settings interact in the server. Hosts with a higher priority value override the settings of those with lower priorities, if they intersect. 

For example: say that we have a zone, where block breaking is allowed, and we have disabled block breaking in the global host. As a result, we can break blocks in the zone, but we can't break them outside of it. This is because our zone overrides the settings of the global host.

**Example:** We have a zone called `castle` with disabled block placement and breakage, and we have made another zone inside of it called `sandbox`, which we want to be a free-build area. If you set `sandbox`'s priority to be larger than the priority of `castle`, then we can explicitly allow block breakage and placement in `sandbox`. As a result, we can break and place blocks in our sandbox, even though these actions are disabled in the castle as a whole.

> You can explicitly set a priority to a zone with `/nope setpriority [-z <zone name>] <priority>`, where `priority` is a non-negative number. You can also explicitly set a priority for a zone when creating one by appending `-p priority` to the command, where `priority` is your given priority.

## Targeting

Our final section is about Nope's targeting system. It's useful when you want some users to be an exception from a zone's setting.

**Example:** making an user to be an exception to a setting

Let's begin by setting a target type for a setting. We can do it with `/nope target type [-z <zone-name>] <setting> <blacklist|whitelist>`. In our case the setting will be `block-break`, and we will have a whitelist. To add users to our new whitelist, we can do `/nope target add player <setting> <player-name>`, where the `setting` is exactly the same setting we assigned a whitelist to. This player will now be the *only player affected* by this setting, because this player is specifically *whitelisted* to this setting's effect. You will see that the chosen user will not be able to break blocks.

> You can also add users with a certain permission using `/nope target add permission <setting> <permission> <setting-value>`. You can remove users and permissions with `/nope target remove <user|permission> <setting> <username|permission-name>`.

## Player-Restrictive Settings 

Some settings are player restrictive. You can see which ones these are using the `/nope settings` command. Any non-default behavior will be ignored by anyone with a the `nope.unrestricted`. However, any non-restrictive type setting will effect everyone, regardless of their permission status.
