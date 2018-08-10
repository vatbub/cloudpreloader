# CloudPreloader
Did you ever find yourself in the situation that you wanted to download a huge file but had no time to do so right then? CloudPreloader might come in handy for you in such situations: It takes the file and sends it to your favourite cloud provider. You can then use your cloud provider's client to download the file seamlessly in the background.

## Currently supported cloud providers
- Dropbox
- IFTTT*

*CloudPreloader can trigger applets through the WebHooks channel. You need to create applets that forward that event together with the URL to your cloud provider (See below for more info).

## Cloud providers that we are currently working on
- OneDrive

## Download
This is embarrassing to say, but there are no downloads available yet. Right now, you need to build CloudPreloader from source and this is not too simple, unfortunately.

## Building from source
Building is not too complicated, but the first build will take time (you were warned!)

What you need:

- jdk 1.8
- Maven
- Free space on your disk for the dependencies

How to build:

1. Clone the repo (`git clone ...`)
2. `cd` into the project root directory
3. Run `mvn install`
4. Wait for all dependencies to download and for the build to finish

Maven will download all dependencies for you, but the download will take some time (we're speaking from experience ;) ). Fortunately, this will only be the case during the first build.

## How to run
Once the build has finished, run the following

    cd pcclient
	cd target
	java -jar cloudpreloader.pcclient-1.0-SNAPSHOT-jar-with-dependencies.jar
	
and you are ready to go :)

## Setting IFTTT up
While all the other cloud providers are self-explanatory, IFTTT is not (at least not in this case). The first thing that you are asking yourself right now is (probably): Wait, IFTTT is not a cloud provider? No, IFTTT is not a cloud provider, but IFTTT can connect to various other cloud providers. CloudPreloader is therefore able to connect to IFTTT but you need to tell IFTTT what to do with the files:

1. Go to [ifttt.com](https://ifttt.com) and sign in/sign up
2. [Create a new applet](https://ifttt.com/create)
3. Select the WebHooks channel as the trigger.
4. The event name must be `cloudpreloader.uploadfile`
5. Select OneDrive as the action (or any other cloud provider that suports IFTTT)
6. Select 'Add file from URL'
7. Click the 'Add ingredient' button next to the 'File URL' field and select `Value1` for the file URL.
8. Click the 'Add ingredient' button next to the 'File name' field and select `Value2` for the file name.
9. Click 'Create action'
10. Review the applet name and click 'Finish'
11. You're done :)


