# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Architecture Diagram

[![Sequence Diagram](diagram.png)](https://sequencediagram.org/index.html?presentationMode=readOnly&shrinkToFit=true#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdOUyABRAAyXLg9RgdOAoxgADFwjUALJCjqYRnElknUG4lTlNA+BAIHEiFRshXM0kwECQuQoamUWkdO6zF5vDizH7bBnaRXqNnGcoASQActzwoLhaNbfpXktHZttjBffUarLaGC9acDSTVOUTSgzQofGBUlahiKUC6iYbWadPdG-VyA-HboXZsAc6l6hAANboSuxmCN3Py12l5WFRMoco95tt9A6qh43KnQEXMoRKFIqCRVSa5iQxEw+HLmGo9GxGBQbHz4GDorUS4wa7WwuTcqrJ5jlvttAPqb7KeUC-ZczlAAmJwnG6W8C1GD8nymF8J3fGBHy-dAOFMLxfH8AJoHYckYB5CBoiSAI0gyLJkHMNkjmvao6iaVoDHUBI0FAoMUBDBYlgOCiQUKM9rzAusIM+J47SWXY+gBc5zxVXURxgBA8KQNBYVw-CDwxbFh31ftUzJCkLThZjiyZVMPQ5cheX5QM71FCVpVrPsS2M2ch1VGS9KnGdnOk8oSiQAAzSwqlDe1YT01iw3edSXM0hyWXTU1MmzXN834ot7KMpVy1M31-Us8DDBjOMxzSt0yyk6c1W7JtX0nDSnLOIFr2UhS1w3WT5MUpq0FUo8T0wHiQTK3iBissZ4M-L4YLfSCv0439SIwQDgNA4a8umiaqtgtaDiQlDvD8QIvBQDtlN8ZhCPSTJMD-PJPJKcoKmkcz6i5ZoWjo1QGO6SaauKH85wkobvvff4+oBrjL3KmS5PsU6lLw07uqxdykwJLTYpgckwES1JGVhIHDJKkzOXMgVKtzaqkhsmUipTDKyvBGBseRwxkzRo0OBQbgEqbEKOgJgdMvKR6+WesnxzfcVJRlG5ioF+mKuY5mL368oTpzFqEGYaHYbVsBEePU8wYvTjFqcb8BsKa6wFNsxOF2tDAkhDgcOhGAAHFC1Zc7iKu+bmEGxcKjdrkaJaexCy+ja33NtkVZvPogdE8SGvB4dymQWIPdGVQlOhRHIuk6L0tJHz-OxvGo-QfnHMKCtuT5Umgcl2yabZ0rboZpnatRmKjV8hSOCztRYWrjLa9M+uLIxwsvWkWYiMyPSbW6GAdAQUBWyX+tp9GH1C2bmVw+z2XHPlmSj5QWf54u80RumFe143rfgx3lA99GJW6rjnk87UVqM7ALnWI+sTwwFBinY2v1Fz9AvrPcYMAACMAEADMAAWJ4C9b6rQmDMFefRH4gE3iNFYXwL7vxHH0MSMc6pW1NqBKYF9VDwIqDAme0h4FILQRgm+z97iUPvkkfB69CG8JIU8MhhYcF7EaNtO2ng9roWwD4KA2BuDwHioYIeKQb6+xyP7W6lFagNFehfSO5NYKgQkaMDiUDY5gyuAnSuwMoJ9CsXwr8-VyIuTipmTIQ9YRD3zp-Huxc0yl0sOXfGJ8x7smJg3QUTcqZi2ie6OqadGZNmCTINu6oUDJH8W40eqTx7eirDWNxnY4wXxSe3CGDNYHSE-v9FO5RAl-01q-AJ+8QGGwgWkqBVwGFsI4Sg1BMAbFXgtvAP2dDehDNGHA8onCxmyOQvIh2ARLCczkskGAAApCACl3b7wCAQ1suiyIB3ulUSkodTHBCcaBVRwAtlQDgBAOSUBZgNImccZpC4rjPNee8z53y2HJwXF4ryMAABWhzFIHOaigNEaksm0xLpQPyESeZRPRWWEpZl4liwpgfZJeLIHQq7lFVmvc0zGlSCgQhYpoAFLYUU-FsTCVTwaaS6p5L+nQqHminJsL4X+KHrMZitp17Ao+dAdlRNKw5RgECygILoCVNfjUilkNyjHI-rVf5wJyiIq6u05gcKFKwlNT08BkKBV3RgEBM2s0aEzKdcBW2azUL7QCF4F50zMywGANgVRhB4iJG0T7K2ULHUPSei9VoxhqHcXsWAzxZ90zcDwCPYVtKs1Br0jnBVgsZAJtrKyJJMt+WZpgEWvNoSC14CHsW7VpbhYhy5K-StUstU1o7hVFtDaSpNqgOXEtBKO2iyBj2lumT+11IqtjaASAABeEa0AaENamlpga8Aa2YCAbNcJen2quR6l1ti3V6JtjtIAA)

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
