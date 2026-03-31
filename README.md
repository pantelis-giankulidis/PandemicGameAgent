# Pandemic Game Agent
The implementation code of the agent participated in the internal competition for the pandemic game, a project requirement for the PLH512 course (Multiagent Systems)
in the Electrical And Computer Engineering Department at the Technical University of Crete.
The report of the project is [here](https://drive.google.com/file/d/1RxTv3O6FRtJrbJhWC_zSzDeA3LnJLJBC/view?usp=share_link)

### How to run
To begin the pandemic game simulation on your local machine, you must first initialize the game server and then connect the four required players. The process involves running two separate Java programs: Server.java and Client.java.

**Step 1: Launching the Game Server** \
The core of the simulation is managed by the server. The Server.java file contains the main entry point for this process, a standard `public static void main` method, which is responsible for setting up the game world, managing the game state, and awaiting player connections.You can run the server using one of two primary methods. 

a) <ins>Using an Integrated Development Environment (IDE)</ins> \
Open your project in a Java IDE such as IntelliJ IDEA, Eclipse, or Visual Studio Code.
Navigate to the Server.java file in the project explorer.
Right-click on the file and select the option to "Run 'Server.main()'".
The IDE will automatically compile the necessary files and start the server process. You should see output in the console window indicating that the server is running and waiting for players to connect.

b) <ins>Using the Command Line</ins> \
Open your terminal or command prompt and navigate to the root directory of your project where the .java files are located.
First, you need to compile the Java source code into bytecode. Run the following command : `javac Server.java`

This will create a Server.class file in the same directory.
Next, run the compiled program with the following command : `java Server`

The server will now be running in your terminal, and you will see any corresponding log messages printed to the console. Keep this terminal window open, as closing it will terminate the server.

**Step 2: Connecting the Players (Clients)** \
After the server is successfully running, the next step is to connect the four players. The game is designed to be cooperative and will not begin until all four players have joined. The Client.java file contains the code for each player, which handles the connection to the server and implements the player's strategy.To start the game, you must launch four separate instances of the client application.

a) <ins>Using an Integrated Development Environment (IDE)</ins> \
With the server already running from your IDE, navigate to the Client.java file.
Right-click on the file and select "Run 'Client.main()'". This will launch the first player.
You must repeat this exact process three more times. Most modern IDEs allow you to run multiple instances of the same application. Each time you run it, a new client (player) will connect to your local server.

b) <ins>Using the Command Line</ins> \
Ensure the server is still running in its original terminal window.
First, compile the client code by navigating to the project directory and running `javac Client.java`


To connect the four players, you will need to open four new, separate terminal windows.
In each of the four terminals, execute the following command to start a client instance: `java Client`


As each client successfully connects to the server, you should see confirmation messages in both the server's terminal and the individual client terminals. Once the fourth client joins, the game simulation will automatically begin.

### What to watch
In every turn, the server collects the players actions, spreads the players suggestion to the other players and prints the state of the game. \
For example
```
Infecting Paris...
Adding 3 Blue cube(s) to Paris
Blue cubes left: 21
Infecting Miami...
Adding 3 Yellow cube(s) to Miami
Yellow cubes left: 21
Infecting Moscow...
Adding 3 Black cube(s) to Moscow
Black cubes left: 21
Infecting Lima...
Adding 2 Yellow cube(s) to Lima
Yellow cubes left: 19
Infecting Manila...
Adding 2 Red cube(s) to Manila
Red cubes left: 22
Infecting Instabul...
Adding 2 Black cube(s) to Instabul
Black cubes left: 19
Infecting Kinshasa...
Adding 1 Yellow cube(s) to Kinshasa
Yellow cubes left: 18
Infecting St. Petersburg...
Adding 1 Blue cube(s) to St. Petersburg
Blue cubes left: 20
Infecting Riyadh...
Adding 1 Black cube(s) to Riyadh
Black cubes left: 18

*** This is round 1.0 ***

 ----- Black Cities  ----- 
Algiers[0, 0, 0, 0]
Baghdad[0, 0, 0, 0]
Cairo[0, 0, 0, 0]
Chennai[0, 0, 0, 0]
Delhi[0, 0, 0, 0]
Instabul[2, 0, 0, 0]
Karachi[0, 0, 0, 0]
Kolkata[0, 0, 0, 0]
Moscow[3, 0, 0, 0]
Mumbai[0, 0, 0, 0]
Riyadh[1, 0, 0, 0]
Tehran[0, 0, 0, 0]

 ----- Yellow Cities  ----- 
Bogota[0, 0, 0, 0]
Buenos Aires[0, 0, 0, 0]
Johannesburg[0, 0, 0, 0]
Khartoum[0, 0, 0, 0]
Kinshasa[0, 1, 0, 0]
Lagos[0, 0, 0, 0]
Lima[0, 2, 0, 0]
Los Angeles[0, 0, 0, 0]
Mexico City[0, 0, 0, 0]
Miami[0, 3, 0, 0]
Santiago[0, 0, 0, 0]
Sao Paulo[0, 0, 0, 0]

 ------ Blue Cities  ------ 
Atlanta[0, 0, 0, 0]
Chicago[0, 0, 0, 0]
Essen[0, 0, 0, 0]
London[0, 0, 0, 0]
Madrid[0, 0, 0, 0]
Milan[0, 0, 0, 0]
Montreal[0, 0, 0, 0]
New York[0, 0, 0, 0]
Paris[0, 0, 3, 0]
San Fransisco[0, 0, 0, 0]
St. Petersburg[0, 0, 1, 0]
Washington[0, 0, 0, 0]

 ------ Red Cities  ------ 
Bangkok[0, 0, 0, 0]
Beijing[0, 0, 0, 0]
Ho Chi Minh City[0, 0, 0, 0]
Hong Kong[0, 0, 0, 0]
Jakarta[0, 0, 0, 0]
Manila[0, 0, 0, 2]
Osaka[0, 0, 0, 0]
Seoul[0, 0, 0, 0]
Shanghai[0, 0, 0, 0]
Sydney[0, 0, 0, 0]
Taipei[0, 0, 0, 0]
Tokyo[0, 0, 0, 0]

 ----- Aggregate Info After Set of Actions ----- 
Cubes Left : [18, 18, 20, 22]
Cards To Draw Left : 44
Disease Status : [Active, Active, Active, Active]
Outbreak Status: 0 outbreaks have already occured (max is 8 outbreaks)
Epidemic Status : 4 Epidemic cards left out of 4

User_0 is playing now as the Scientist

User_1 is thinking of a suggestion to make..
```

Furthermore, each player print it's status and the way it operates. For example, in the terminal or IDE running one of the player you would expect to see somthing like 
```
Connected to server!

Hey! My username is User_1

Hey! My role is Operations Expert
Player   0  role  Medic
Player   1  role  Operations Expert
Player   2  role  Quarantine Specialist
Player   3  role  Scientist
Reading suggestions of the player 0
Nothing to read
Reading suggestions of the player 2
Nothing to read
Reading suggestions of the player 3
Nothing to read

*** This is round 1.0 and User_0 is playing now *** 

Server: User1 please import your suggestion for this turn..

My current hand...
Seoul
Jakarta

My hand's color count...
Black cards count: 0
Yellow cards count: 0
Blue cards count: 0
Red cards count: 2
User_1 treated 1 Yellow cube from Miami
User_1 treated 1 Yellow cube from Lima
User_1 treated 1 Yellow cube from Miami
User_1 treated 1 Yellow cube from Lima
User_1 treated 1 Yellow cube from Miami
User_1 treated 1 Yellow cube from Miami
User_1 treated 1 Yellow cube from Miami
User_1 treated 1 Yellow cube from Miami
User_1 treated 1 Yellow cube from Miami
User_1 treated 1 Yellow cube from Miami
User_1 treated 1 Yellow cube from Miami
User_1 treated 1 Yellow cube from Miami
User_1 treated 1 Yellow cube from Miami
User_1 treated 1 Yellow cube from Miami
User_1 treated 1 Yellow cube from Lima
User_1 treated 1 Red cube from Manila
User_1 treated 1 Red cube from Manila
User_1 treated 1 Red cube from Manila
Considering 450 possible Actions
Heuristics for action calculated
```
By the end of the simulation, you can see if the players managed to cooperate, erase the pandemic spread and win, or they failed and the pandemic won

### The players strategy
Each agent strategy implementation follows a reinforcement learning approach to find the
best policy for the agent. It uses some heuristics about
the game,an ’adaptive play’ part that tries to learn the
impact of an action when it is followed by a sequence of
actions of the other agents and a belief network about
the propabilities of other agent’s actions. A more detailed explanation of the strategy can be read in the [official report](https://drive.google.com/file/d/1RxTv3O6FRtJrbJhWC_zSzDeA3LnJLJBC/view?usp=share_link) of the implementation.

### The results
The team managed to qualify to the quarterfinals in the class competition, where 23 teams participated. 