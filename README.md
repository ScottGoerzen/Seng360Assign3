# Seng360Assign3
Assignment 3 For Seng 360 Fall 2017

This Program uses 3 terminal windows.

First open all windows to the Seng360Assign3 folder and run in the first

	rmiregistry

On one terminal window compile all files with the command 

	javac GrootChat/RMIInterface.java GrootChat/RMICInterface.java GrootChat/ServerOperation.java GrootChat/ClientOperation.java

On the same window run 

	java GrootChat/ServerOperation

And enter the password 'GROOT' before selecting the desired security options.

On the last window run 

	java GrootChat/ClientOperation 

And enter a arbitrary Username and the password 'Rocket' to authenticate the user before selecting the desired security options.

*Note that the server must be run first, and the client to follow, but the client can be run at any time, and can reconnect any number of times.*

You can now type freely as the Client or Server to send messages to the other party.

Both client and server connections are ended and stopped by typing the -Quit command instead of a message into the command line. If the server quits with a client still connected, it kicks the client out.

Security options can be changed by the server only when there is no client connected.
This is done by typing a '-' followed by a command into the input field.

Commands include:

	-ct	: Confidentiality true : Turns on encryption of outgoing messages

	-cf	: Confidentiality false : Turns off encryption of outgoing messages

	-it 	: Integrity Checks enabled : Turns on checking of message integrity via message authentication codes (MAC)

	-if 	: Integrity Checks disabled : Turns off checking of message integrity via message authentication codes (MAC)

	-at	: Availability true : Turns on checking of real users via public/private key encryption

	-af	: Availability false : Turns off checking of real users via public/private key encryption

All three security options are enabled by default, but are selectable and set by the initiation sequence in the menu feature.

Authorization is checked at login on both Server and Client side by means of comparing the hash of the input password to the hash saved in an access controlled directory.

When the client and the server connect to each other, they send a authentication message via public/private keys in asymmetric cryptography and when the client contacts the server, a session key established for symmetric cryptography with AES is returned.

All later encryption is done by AES encryption with the session key established at the first step.

Hashing is done via the MD5 hashing scheme.

Integrity is verified by means of passing a message authentication code (MAC)  along with the message. The MAC consists of the message being hashed with MD5, encrypted with AES and then converted back to a string.

To explore the JavaDocs for our program, open index.html in the JavaDocs folder.
