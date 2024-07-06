# About Live Maid Mod

Hi, this is the repo of fabric mod "Live Maid", which is aimed to be a all-in-one streaming bot that follows player, syncs danmuku and other stream room messages, and listens to chat commands.

## What Can This Mod DO

The mod is in primitive stage, and thus, only basic features have been implemented. They are listed as below:

* As the role of maid:

  * Bind and connect to BILIBILI live room;
  * Receive and sync live room messages to 'master' player;
  * Recieve chat bar messages from 'master' player and display it on the bubble upon master player;
  * Make actions to live room chat commands:
    * w/s/a/d to *move* view port;
    * +/- to zoom view port;
  * Follow master player's position and view angle, with smoothing functions to avoid shrinks and laggs:
    * Position following function uses velocity to smooth;
    * View angle uses linear lerp to smooth;
  * Save configuration to local config file;
  * Filter-out /tell messages from master to maid, and from maid to master ---
    * If the message seems to be not caused by LiveMaid mod Master-Maid message pipe, then it will be displayed as well.

* As the role of master:

  * Send chat-bar(screen) content to maid account when it changes;
  * Receive and display live room messages when received from maid player;
  * Save configuration to local config file;
  * Filter-out /tell messages from master to maid, and from maid to master ---
    * If the message seems to be not caused by LiveMaid mod Master-Maid message pipe, then it will be displayed as well.
   
## How Does This Mod Work

The mod can now be seperated into 2 parts, which is the message pipe between master player and maid player, and the view port following function.

### The Message Pipe

The master player and maid player need to communicate with each other in order to display and send messages (e.g., live room messages or master's chat input content). 
Therefore, we uses minecraft's /tell command to send content to send messages between the 2 players.

As the feature of the command, the message will not be noticed by other player, and it also requires no other ports/functions to make this become true.
Though the message will be logged into the server's log, but if you care about that, you can encode the message using certain encryption methods, and decode it as well.

Since most servers allows players to somehow send private messages with each other, the mod works for nearly all minecraft server. 
(Unless the operator banned /tell command and/or provides no method to send private chat to other players)

### View Port Following

Even the Message Pipe functions is banned by the server, the mod still can have full functions about following certain player. 
This is done by acquiring player entity of the player, and uses maths to calculate the target position of the maid player, and send movement packages to the server.
It is all done using single client, and hence, as long as the server allows other player to be seen, this function can have it full function.

However, since the function is fully client-side, certain drawbacks is notable:
* If the network is bad, the position of the target player can have huge laggs, so the viewport following result can be not stable.
* You can only follow player near you. As long as the player is not in entity display range set by the server, you can not follow him/her.
* Currently, it is not possible to follow player when him/her change the world it stayed, this can be solve later.

## Planned Feature List Explained

The following features are planned by the developer, and will be implemented in the furture:

* Multi master, and multi maid configuration;
* More chat command (such as change following player, change world, etc.);
* Change world and change target player;
* First person view and Second person view;
* No following view and its live chat command controlls;
* Basic permission configuration, and restriction to live chat command (such as not allowed world / not allowed player etc.);
* Bilibili cookie implementation to acquire logged in account features, including send chat, banned account and other management functions;

The mod has a target to be a live bot which can stay in server for 24 hours long and live stream what player do in the server.

## Require Feature / Report Bug

Please use github's issue.

# Special Thank

We give special thanks to Fabric Mod [TalkBubble](https://github.com/Globox1997/TalkBubbles/) for its display and chat implementations.

We gibe special thanks to Fabric Mod [Fokuso](https://github.com/QazCetelic/Fokuso/) for its chat message filtering implementations.

We give special thanks to [Chuanwise](https://github.com/chuanwise) for his suggestions on chat bar content sync featurs, and supports on building and packing the project with gradle and shadowjar.
