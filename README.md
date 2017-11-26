# HubManager
Manages the hub of the Pocket Pixels server

This plugin is designed to allow players to switch between servers through bungee cord. To the items provided to the player can be configured by adding `.menu` files to the config folder with the following structure:

```
{
	"title":"Title",
	"itemName": "Website",
	"slot": 6,
	"items":[
				{
					"name": "",
					"icon": 1,
					"itemdat": 0,
					"lore": ["Come with me!"],
					"command": "message http://www.example.net/",
					"x": 0,
					"y": 0
				}
			],
	"size": 0
}
```

There are a few key fields here:

 - `title`: The name of the menu that appears at the top of the chest interface
 - `itemName`: The name that appears on the item when selected
 - `slot`: The location within the hotbar that the item should be placed
 - `items`: An array of the items to appear in this cheat menu. If only one item is given, no chest will be opened and that item command will be executed immediately
   - `name`: The name of the option
   - `icon`: The id of the icon to be used for this option
   - `itemdat`: The meta for this option (allows use of stained glass for example)
   - `lore`: An array of strings, with any additional information to appear below the item. If any entry contains the phrase `{{current}}`, that will be replaced with the total number of players on the server
   - `command`: The command to be executed when this item is clicked. There are currently two commands: `message <message>` will send a message to the player and `connect <server name>` will connect the player to a new server
   - `x`: The x location for the item within the chest
   - `y`: the y location for the item within the chest
 - `size`: The size of the chest inventory to open, 0 will simply execute the first item in the list without opening a chest interface.
 
 In addition, the plugin has a login system that prompts players with staff permissions to login before they can execute any commands or move around. Their passwords are stored using a java library called `jasypt` which handles all cryptographic details.
