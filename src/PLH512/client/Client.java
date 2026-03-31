package PLH512.client;

import java.io.*; 
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import PLH512.server.Board;
import PLH512.server.City;

public class Client  
{
    final static int ServerPort = 64240;
    final static String username = "myName";
  
    public static void main(String args[]) throws UnknownHostException, IOException, ClassNotFoundException  
    { 
    	int numberOfPlayers;
    	int myPlayerID;
    	String myUsername;
    	String myRole;
    	
        
        // Getting localhost ip 
        InetAddress ip = InetAddress.getByName("localhost"); 
          
        // Establish the connection 
        Socket s = new Socket(ip, ServerPort); 
        System.out.println("\nConnected to server!");
        
        // Obtaining input and out streams 
        ObjectOutputStream dos = new ObjectOutputStream(s.getOutputStream());
        ObjectInputStream dis = new ObjectInputStream(s.getInputStream());  
        
        // Receiving the playerID from the Server
        myPlayerID = (int)dis.readObject();
        myUsername = "User_" + myPlayerID;
        System.out.println("\nHey! My username is " + myUsername);
        
        // Receiving number of players to initialize the board
        numberOfPlayers = (int)dis.readObject();
        
        // Receiving my role for this game
        myRole = (String)dis.readObject();
        System.out.println("\nHey! My role is " + myRole);
        
        // Sending the username to the Server
        dos.reset();
        dos.writeObject(myUsername);
        
        // Setting up the board
        Board[] currentBoard = {new Board(numberOfPlayers)};
        
        //Setting up past players actions
        ArrayList<Action> adaptivePlayMatrix = new ArrayList<Action>();
        
        
        ArrayList<AgentActionProbability> agentBNetworks=new ArrayList<AgentActionProbability>();
        //Setting up belief state about other agents
        for(int i=0;i<4;i++) {
        	if(i==myPlayerID) {
        		agentBNetworks.add(null);
        	}
        	agentBNetworks.add( new AgentActionProbability(i,currentBoard[0].getRoleOf(i)));
        }
        
        // Creating sendMessage thread 
        Thread sendMessage = new Thread(new Runnable()  
        { 
            @Override
            public void run() {
            	
            	boolean timeToTalk = false;
            	
            	int k=18; // Hyperparameter
            	double discount_factor=0.8;//hyperparameter
            	int m=8; // Hyperparameter
            	
                while (currentBoard[0].getGameEnded() == false) 
                { 	
                	timeToTalk = ((currentBoard[0].getWhoIsTalking() == myPlayerID)  && !currentBoard[0].getTalkedForThisTurn(myPlayerID));
                	
                	if(currentBoard[0].getRoleOf(0)=="Medic") {
                		
                	}
                	try {
						TimeUnit.MILLISECONDS.sleep(15);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
                	
                    try { 
                        // Executing this part of the code once per round
                        if (timeToTalk)
                        {
                        	
                        	// Initializing variables for current round
                        	
                        	Board myBoard = currentBoard[0];
                        	
                        	String myCurrentCity = myBoard.getPawnsLocations(myPlayerID);
                        	City myCurrentCityObj = myBoard.searchForCity(myCurrentCity);
                        	
                        	ArrayList<String> myHand = myBoard.getHandOf(myPlayerID);
                        	
                        	int[] myColorCount = {0, 0, 0, 0};
                        	
                        	for (int i = 0 ; i < 4 ; i++)
                        		myColorCount[i] =  cardsCounterOfColor(myBoard, myPlayerID, myBoard.getColors(i));
                        	
                        	ArrayList<citiesWithDistancesObj> distanceMap = new ArrayList<citiesWithDistancesObj>();
                        	distanceMap = buildDistanceMap(myBoard, myCurrentCity, distanceMap);
                        	
                        	
                        	String myAction = "";
                        	int myActionCounter = 0;
                        	
                        	// Printing out my current hand
                        	System.out.println("\nMy current hand...");
                        	printHand(myHand);
                        	
                        	// Printing out current color count
                        	System.out.println("\nMy hand's color count...");
                        	for (int i = 0 ; i < 4 ; i++)
                        		System.out.println(myBoard.getColors(i) + " cards count: " + myColorCount[i]);
                        	
                        	boolean tryToCure = false;
                        	String colorToCure = null;
                        	
                        	boolean tryToTreatHere = false;
                        	String colorToTreat = null;
                        	
                        	boolean tryToTreatClose = false;
                        	String destinationClose = null;
                        	
                        	boolean tryToTreatMedium = false;
                        	String destinationMedium = null;
                        	
                        	String destinationRandom = null;
                        	
                        	
                        	/** During experimentation,we set the if condition below to actual playerID.Therefore,we have one or more
                        	 * agents following a naive policy and one or more following the policy of our algorithm.
                        	 */
                        	if(myPlayerID==512) {
                        		
                        	if (myColorCount[0] > 4 || myColorCount[1] > 4 || myColorCount[2] > 4 || myColorCount[3] > 4)
                        	{
                        		if (myActionCounter < 4)
                        			tryToCure = true;
                        		
                        		if (myColorCount[0] > 4)
                        			colorToCure = "Black";
                        		else if (myColorCount[1] > 4)
                        			colorToCure = "Yellow";
                        		else if (myColorCount[2] > 4)
                        			colorToCure = "Blue";
                        		else if (myColorCount[3] > 4)
                        			colorToCure = "Red";
                        	}
                        	
                        	if (tryToCure)
                        	{
                        		System.out.println("I want to try and cure the " + colorToCure + " disease!");
                        		myAction = myAction + toTextCureDisease(myPlayerID, colorToCure);
                        		myBoard.cureDisease(myPlayerID, colorToCure);
                        		myActionCounter++;
                        		
                        	}
                        	
                        	if (myCurrentCityObj.getBlackCubes() != 0 || myCurrentCityObj.getYellowCubes() != 0  || myCurrentCityObj.getBlueCubes() != 0  || myCurrentCityObj.getRedCubes() != 0)
                        	{
                        		if (myActionCounter < 4)
                        			tryToTreatHere = true;
                        		
                        		if (myCurrentCityObj.getBlackCubes() > 0)
                        			colorToTreat = "Black";
                        		else if ( myCurrentCityObj.getYellowCubes() > 0)
                        			colorToTreat = "Yellow";
                        		else if (myCurrentCityObj.getBlueCubes() > 0)
                        			colorToTreat = "Blue";
                        		else if (myCurrentCityObj.getRedCubes() > 0)
                        			colorToTreat = "Red";
                        	}
                        	
                        	if (tryToTreatHere) 
                        	{
                        		while (myCurrentCityObj.getMaxCube() != 0 && myActionCounter < 4)
                        		{
                        			colorToTreat = myCurrentCityObj.getMaxCubeColor();
                    				
                    				System.out.println("I want to try and treat one " + colorToTreat + " cube from " + myCurrentCity + "!");
                    				
                    				myAction = myAction + toTextTreatDisease(myPlayerID, myCurrentCity, colorToTreat);
                            		myActionCounter++;
                            		
                            		myBoard.treatDisease(myPlayerID, myCurrentCity, colorToTreat);
                        		}
                        	}
                        	
                        	if (myActionCounter < 4 )
                        	{
                        		destinationClose = getMostInfectedInRadius(1, distanceMap, myBoard);
                        		
                        		if(!destinationClose.equals(myCurrentCity))
                        			tryToTreatClose = true;
                    		}
                        	
                        	if (tryToTreatClose)
                        	{
                        		System.out.println("Hhhmmmmmm I could go and try to treat " + destinationClose);
                        		
                        		myAction = myAction + toTextDriveTo(myPlayerID, destinationClose);
                        		myActionCounter++;
                        		
                        		myBoard.driveTo(myPlayerID, destinationClose);
                        		
                        		myCurrentCity = myBoard.getPawnsLocations(myPlayerID);
                            	myCurrentCityObj = myBoard.searchForCity(myCurrentCity);
                        		
                        		while (myCurrentCityObj.getMaxCube() != 0 && myActionCounter < 4)
                        		{
                        			colorToTreat = myCurrentCityObj.getMaxCubeColor();
                    				
                    				System.out.println("I want to try and treat one " + colorToTreat + " cube from " + myCurrentCity + "!");
                    				
                    				myAction = myAction + toTextTreatDisease(myPlayerID, myCurrentCity, colorToTreat);
                            		myActionCounter++;
                            		
                            		myBoard.treatDisease(myPlayerID, myCurrentCity, colorToTreat);
                        		}
                        	}
                        	
                        	
                        	if (myActionCounter < 4 )
                        	{
                        		destinationMedium = getMostInfectedInRadius(2, distanceMap, myBoard);
                        		
                        		if(!destinationMedium.equals(myCurrentCity))
                        			tryToTreatMedium = true;
                    		}
                        	
                        	if (tryToTreatMedium)
                        	{
                        		System.out.println("Hhhmmmmmm I could go and try to treat " + destinationMedium);
                        		
                        		String driveFirstTo = getDirectionToMove(myCurrentCity, destinationMedium, distanceMap, myBoard);
                        		
                        		myAction = myAction + toTextDriveTo(myPlayerID, driveFirstTo);
                        		myActionCounter++;
                        		myAction = myAction + toTextDriveTo(myPlayerID, destinationMedium);
                        		myActionCounter++;
                        		
                        		myBoard.driveTo(myPlayerID, driveFirstTo);
                        		
                        		myCurrentCity = myBoard.getPawnsLocations(myPlayerID);
                            	myCurrentCityObj = myBoard.searchForCity(myCurrentCity);
                        		
                        		myBoard.driveTo(myPlayerID, destinationMedium);
                        		
                        		myCurrentCity = myBoard.getPawnsLocations(myPlayerID);
                            	myCurrentCityObj = myBoard.searchForCity(myCurrentCity);
                        		
                        		while (myCurrentCityObj.getMaxCube() != 0 && myActionCounter < 4)
                        		{
                        			colorToTreat = myCurrentCityObj.getMaxCubeColor();
                    				
                    				System.out.println("I want to try and treat one " + colorToTreat + " cube from " + myCurrentCity + "!");
                    				
                    				myAction = myAction + toTextTreatDisease(myPlayerID, myCurrentCity, colorToTreat);
                            		myActionCounter++;
                            		
                            		myBoard.treatDisease(myPlayerID, myCurrentCity, colorToTreat);
                        		}
                        	}
                        	
                        	Random rand = new Random();
                        	
                        	
                        	while (myActionCounter < 4)
                        	{
                        		int upperBound;
                        		int randomNumber;
                        		String randomCityToGo;
                        		
                        		upperBound = myCurrentCityObj.getNeighboursNumber();
                        		randomNumber = rand.nextInt(upperBound);
                        		randomCityToGo = myCurrentCityObj.getNeighbour(randomNumber);
                        		
                        		System.out.println("Moving randomly to " + randomCityToGo);
                        		
                        		myAction = myAction + toTextDriveTo(myPlayerID, randomCityToGo);
                        		myActionCounter++;
                        		
                        		myBoard.driveTo(myPlayerID, randomCityToGo);
                        		
                        		myCurrentCity = myBoard.getPawnsLocations(myPlayerID);
                            	
                        	}}else {
                        		ArrayList<Action> next=new ArrayList<Action>();
                        		State s=new State(myBoard);
                        		
                        		next = expandGame(myBoard,s,myPlayerID); // get all possible actions
                        		
                        		System.out.println("Considering "+next.size()+" possible Actions");
                        		
                        		for(Action a:next) {
                        			if(myBoard.getRound()<k) {
                        				a.heuristicReward(myBoard, 1);
                        				System.out.println("Heuristics for action calculated");
                        			}
                        			else {
                        				a.heuristicReward(myBoard, 1-(myBoard.getRound()-k)/20);
                        				a.adaptivePlayR((myBoard.getRound()-k)/20,adaptivePlayMatrix,m);
                        			}
                        			a.beliefStateQ(discount_factor,0,agentBNetworks.get(0),agentBNetworks.get(1),agentBNetworks.get(2),copyBoard(myBoard));
                        			//a.Q=a.getR();
                        			//System.out.println("Updated q for action is "+a.getR());
                        		}
                        		Action picked=getMaxQAction(next);
                        		
                        		System.out.println("BEFORE MOVE "+myBoard.getPawnsLocations(myPlayerID));
                        		
                        		for(int i=1;i<5;i++) {
                        			myAction=myAction+picked.getMoveMessage(i);
                        			myBoard=picked.getBoard(i);
                        		}
                        		System.out.println("AFTER MOVE "+myBoard.getPawnsLocations(myPlayerID));
                        		next.clear();
                    		}
                        	
                        	String msgToSend;
                        	if (myBoard.getWhoIsPlaying() == myPlayerID)
                        		msgToSend = myAction;
                            else 
                        		msgToSend = myAction + ",This was my recommendation"; //"Recommendation"
                        	
                        	// Writing to Server
                        	dos.flush();
                        	dos.reset();
                        	if (msgToSend != "")
                        		msgToSend = msgToSend.substring(1); // Removing the initial delimeter
                        	dos.writeObject(msgToSend);
                        	System.out.println(myUsername + " : I've just sent my " + msgToSend);
                        	currentBoard[0].setTalkedForThisTurn(true, myPlayerID);
                        }
                    } catch (IOException e) { 
                        e.printStackTrace(); 
					}
                } 
            } 
        }); 
          
        // Creating readMessage thread 
        Thread readMessage = new Thread(new Runnable()  
        { 
            @Override
            public void run() { 
            	
            	
                while (currentBoard[0].getGameEnded() == false) { 
                    try { 
                        
                    	// Reading the current board
                    	currentBoard[0] = (Board)dis.readObject();
                    	
                    	for(int i=0;i<4;i++) {
                    		if(i==currentBoard[0].getWhoIsTalking()) {
                    			continue;
                    		}else {
                    			System.out.println("Reading suggestions of the player "+i);
                    			String s=currentBoard[0].getActions(i);
                    			if(!s.isEmpty()) {
                    				Action a=decodePlayerRecommendations(s,currentBoard[0].getWhoIsTalking(),currentBoard[0]);
                    				/*** After we have decoded other agent's recommendations,we store them in the adaptive play
                    				 * matrix and also use this information to update the belief network for the agent
                    				 */
                    				adaptivePlayMatrix.add(a);
                    				agentBNetworks.get(i).considerPosteriors(a);
                    				System.out.println("Saved players "+i+" recommendations as "+s);
                    			}else {
                    				System.out.println("Nothing to read");
                    			}
                    		}
                    	}
                    	// Read and print Message to all clients
                    	String prtToScreen = currentBoard[0].getMessageToAllClients();
                    	if (!prtToScreen.equalsIgnoreCase(""))
                    		System.out.println(prtToScreen);
                    	
                    	// Read and print Message this client
                    	prtToScreen = currentBoard[0].getMessageToClient(myPlayerID);
                    	if (!prtToScreen.equalsIgnoreCase(""))
                    		System.out.println(prtToScreen);
                    	
                    } catch (IOException e) { 
                        e.printStackTrace(); 
                    } catch (ClassNotFoundException e) {
						e.printStackTrace();
					} 
                } 
            } 
        }); 
        
        // Starting the threads
        readMessage.start();
        sendMessage.start(); 
        
        // Checking if the game has ended
        while (true) 
        {
        	if (currentBoard[0].getGameEnded() == true) {
        		System.out.println("\nGame has finished. Closing resources.. \n");
        		//scn.close();
            	s.close();
            	System.out.println("Recources closed succesfully. Goodbye!");
            	System.exit(0);
            	break;
        }
        
        }
    } 
    
    // --> Useful functions <--
    
    public static Board copyBoard (Board boardToCopy)
    {
    	Board copyOfBoard;
    	
    	try {
    	     ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    	     ObjectOutputStream outputStrm = new ObjectOutputStream(outputStream);
    	     outputStrm.writeObject(boardToCopy);
    	     ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
    	     ObjectInputStream objInputStream = new ObjectInputStream(inputStream);
    	     copyOfBoard = (Board)objInputStream.readObject();
    	     return copyOfBoard;
    	   }
    	   catch (Exception e) {
    	     e.printStackTrace();
    	     return null;
    	   }
    }
    
    public static String getDirectionToMove (String startingCity, String goalCity, ArrayList<citiesWithDistancesObj> distanceMap, Board myBoard)
    {
    	City startingCityObj = myBoard.searchForCity(startingCity);
    	
    	int minDistance = distanceFrom(goalCity, distanceMap);
    	int testDistance = 999;
    	
    	String directionToDrive = null;
    	String testCity = null;
    	
    	for (int i = 0 ; i < startingCityObj.getNeighboursNumber() ; i++)
    	{
    		ArrayList<citiesWithDistancesObj> testDistanceMap = new ArrayList<citiesWithDistancesObj>();
    		testDistanceMap.clear();
    		
    		testCity = startingCityObj.getNeighbour(i);
    		testDistanceMap = buildDistanceMap(myBoard, testCity, testDistanceMap);
    		testDistance = distanceFrom(goalCity, testDistanceMap);
    		
    		if (testDistance < minDistance)
    		{
    			minDistance = testDistance;
    			directionToDrive = testCity;
    		}
    	}
    	return directionToDrive;
    }
    
    
    public static String getMostInfectedInRadius(int radius, ArrayList<citiesWithDistancesObj> distanceMap, Board myBoard)
    {
    	int maxCubes = -1;
    	String mostInfected = null;
    	
    	for (int i = 0 ; i < distanceMap.size() ; i++)
    	{
    		if (distanceMap.get(i).getDistance() <= radius)
    		{
    			City cityToCheck = myBoard.searchForCity(distanceMap.get(i).getName());
    			
    			if (cityToCheck.getMaxCube() > maxCubes)
    			{
    				mostInfected = cityToCheck.getName();
    				maxCubes = cityToCheck.getMaxCube();
    			}
    		}
    	}
    	
    	return mostInfected;
    }
    
    // Count how many card of the color X player X has
    public static int cardsCounterOfColor(Board board, int  playerID, String color)
    {
    	int cardsCounter = 0;
    	
    	for (int i = 0 ; i < board.getHandOf(playerID).size() ; i++)
    		if (board.searchForCity(board.getHandOf(playerID).get(i)).getColour().equals(color))
    			cardsCounter++;
    	
    	return cardsCounter;
    }
    
    public static void printHand(ArrayList<String> handToPrint)
    {
    	for (int i = 0 ; i < handToPrint.size() ; i++)
    		System.out.println(handToPrint.get(i));
    }
    
    public static boolean alredyInDistanceMap(ArrayList<citiesWithDistancesObj> currentMap, String cityName)
    {
    	for (int i = 0 ; i < currentMap.size() ; i++)
    		if (currentMap.get(i).getName().equals(cityName))
    			return true;
    	
    	return false;
    }
    
    public static boolean isInDistanceMap (ArrayList<citiesWithDistancesObj> currentMap, String cityName)
    {
    	for (int i = 0 ; i < currentMap.size() ; i++)
    	{
    		if (currentMap.get(i).getName().equals(cityName))
    			return true;
    	}
    	return false;
    }
    
    public static void printDistanceMap(ArrayList<citiesWithDistancesObj> currentMap)
    {
    	for (int i = 0 ; i < currentMap.size() ; i++)
    		System.out.println("Distance from " + currentMap.get(i).getName() + ": " + currentMap.get(i).getDistance());
    }
    
    public static int distanceFrom(String cityToFind, ArrayList<citiesWithDistancesObj> currentDistanceMap)
    {
    	int result = -1;
    	
    	for (int i = 0 ; i < currentDistanceMap.size() ; i++)
    		if (currentDistanceMap.get(i).getName().equals(cityToFind))
    			result = currentDistanceMap.get(i).getDistance();
    	
    	return result;
    }
    
    public static int numberOfCitiesWithDistance(int distance, ArrayList<citiesWithDistancesObj> currentDistanceMap)
    {
    	int count = 0;
    	
    	for (int i = 0 ; i < currentDistanceMap.size() ; i++)
    		if (currentDistanceMap.get(i).getDistance() == distance)
    			count++;
    	
    	return count;
    }
    
    public static ArrayList<citiesWithDistancesObj> buildDistanceMap(Board myBoard, String currentCityName, ArrayList<citiesWithDistancesObj> currentMap)
    {
    	currentMap.clear();
    	currentMap.add(new citiesWithDistancesObj(currentCityName, myBoard.searchForCity(currentCityName), 0));

    	for (int n = 0 ; n < 15 ; n++)
    	{
        	for (int i = 0 ; i < currentMap.size() ; i++)
        	{
        		if (currentMap.get(i).getDistance() == (n-1))
        		{
        			for (int j = 0 ; j < currentMap.get(i).getCityObj().getNeighboursNumber() ; j++)
        			{
        				String nameOfNeighbor = currentMap.get(i).getCityObj().getNeighbour(j);
        				
        				if (!(alredyInDistanceMap(currentMap, nameOfNeighbor)))
        					currentMap.add(new citiesWithDistancesObj(nameOfNeighbor, myBoard.searchForCity(nameOfNeighbor), n));
        			}
        		}
        	}
    	}
    	
    	return currentMap;
    }
    
    
    // --> Actions <--
    
    
    // --> Coding functions <--
    
    public static String toTextDriveTo(int playerID, String destination)
    {
    	return "#DT,"+playerID+","+destination;
    }
    	
    public static String toTextDirectFlight(int playerID, String destination)
    {
    	return "#DF,"+playerID+","+destination;
    }
    
    public static String toTextCharterFlight(int playerID, String destination)
    {
    	return "#CF,"+playerID+","+destination;
    }
    
    public static String toTextShuttleFlight(int playerID, String destination)
    {
    	return "#SF,"+playerID+","+destination;
    }
    
    public static String toTextBuildRS(int playerID, String destination)
    {
    	return "#BRS,"+playerID+","+destination;
    }
    
    public static String toTextRemoveRS(int playerID, String destination)
    {
    	return "#RRS,"+playerID+","+destination;
    }
    
    public static String toTextTreatDisease(int playerID, String destination, String color)
    {
    	return "#TD,"+playerID+","+destination+","+color;
    }
    
    public static String toTextCureDisease(int playerID, String color)
    {
    	return "#CD1,"+playerID+","+color;
    }
    
    public static String toTextCureDisease(int playerID, String color, String card1, String card2, String card3, String card4, String card5)
    {
    	return "#CD2,"+playerID+","+color+","+card1+","+card2+","+card3+","+card4+","+card5;
    }
    
    public static String toTextShareKnowledge(boolean giveOrTake, String cardToSwap, int myID, int playerIDToSwap)
    {
    	return "#SK,"+giveOrTake+","+cardToSwap+","+myID+","+playerIDToSwap;
    }
    
    public static String toTextActionPass(int playerID)
    {
    	return "#AP,"+playerID;
    }
    
    public static String toTextChatMessage(int playerID, String messageToSend)
    {
    	return "#C,"+playerID+","+messageToSend;
    }
    
    public static String toTextPlayGG(int playerID, String cityToBuild)
    {
    	return "#PGG,"+playerID+","+cityToBuild;
    }
    
    public static String toTextPlayQN(int playerID)
    {
    	return "#PQN,"+playerID;
    }
    public static String toTextPlayA(int playerID, int playerToMove, String cityToMoveTo)
    {
    	return "#PA,"+playerID+","+playerToMove+","+cityToMoveTo;
    }
    public static String toTextPlayF(int playerID)
    {
    	return "#PF,"+playerID;
    }
    public static String toTextPlayRP(int playerID, String cityCardToRemove)
    {
    	return "#PRP,"+playerID+","+cityCardToRemove;
    }
    public static String toTextOpExpTravel(int playerID, String destination, String colorToThrow)
    {
    	return "#OET,"+playerID+","+destination+","+colorToThrow;
    }
    	// Utility functions
    
    /*** After policy evaluation,we act greedy by picking the action with the highest Q
     * 
     * @param List of possible actions,a
     * @return The action with the highest Q in a.
     */
    public static Action getMaxQAction(ArrayList<Action> a) {
    	double maxQ=-100;
    	int indexOfBestAction=0;
    	for(int i=0;i<a.size();i++) {
    		maxQ=maxQ<a.get(i).getQ()?a.get(i).getQ():maxQ;
    		indexOfBestAction=(maxQ==a.get(i).getQ())?i:indexOfBestAction;
    	}
    	return a.get(indexOfBestAction);
    }
    
    /*** Given the board and the player id,compute all possible legal moves
     * 
     * @param b:The Board at the time of inquiry
     * @param player:The player that has plays
     * @return ArrayList of all possible moves,for player player in the board b
     */
    public static ArrayList<Move> nextMove(Board b,int player){
    	ArrayList<Move> possibleMove=new ArrayList<Move>();
    	String currentCityName = b.getPawnsLocations(player);
    	City currentCity = b.searchForCity(currentCityName);
    	
    	// drive to possible moves
		for (int i = 0 ; i < currentCity.getNeighboursNumber() ; i++) {
			possibleMove.add(new Move("driveTo",currentCity.getNeighbour(i)));
		}
		
		// direct flight possible moves 
		for(String city : b.getHandOf(player)) {
			possibleMove.add(new Move("directFlight",city));
		}
		
		// charter flight possible moves 
		if(b.getHandOf(player).contains(currentCityName)) {
			for(int i = 0;i < b.getCitiesCount();i++) {
				for (int j = 0 ; j < currentCity.getNeighboursNumber() ; j++) {
					if(currentCity.getNeighbour(j)==b.searchForCity(i).getName()) {
						continue;
					}
				}
				if(b.searchForCity(i).getMaxCube()>2) {
					possibleMove.add(new Move("charterFlight",b.searchForCity(i).getName()));
				}
			}
		}
		
		// shuttle flight possible moves
		if(b.getRSLocations().contains(currentCityName)){
			for(String rscity:b.getRSLocations()) {
				if(rscity==currentCityName) {
					continue;
				}
				possibleMove.add(new Move("shuttleFlight",rscity));	
			}
		}
		
		// build research station possible move
		if(b.getRoleOf(player)=="Operations Expert") {
			possibleMove.add(new Move("buildRS",currentCityName));
		}
		else{
			if(b.getHandOf(player).contains(currentCityName)) {
				possibleMove.add(new Move("buildRS",currentCityName));
			}
		}
		
		//  Treat dicease possible moves
		if(currentCity.getYellowCubes()>0) {
			possibleMove.add(new Move("treatDisease","Yellow"));
		}
		if(currentCity.getBlackCubes()>0) {
			possibleMove.add(new Move("treatDisease","Black"));
		}
		if(currentCity.getRedCubes()>0) {
			possibleMove.add(new Move("treatDisease","Red"));
		}
		if(currentCity.getBlueCubes()>0) {
			possibleMove.add(new Move("treatDisease","Blue"));
		}
		
		// discover cure possible moves
		if(b.getRSLocations().contains(currentCityName)){
			int yellow=0,black=0,red=0,blue=0;
			for(String ct:b.getHandOf(player)) {
				if(b.searchForCity(ct).getColour()=="Yellow") {
					yellow++;
				}
				if(b.searchForCity(ct).getColour()=="Black") {
					black++;
				}
				if(b.searchForCity(ct).getColour()=="Red") {
					red++;
				}
				if(b.searchForCity(ct).getColour()=="Blue") {
					blue++;
				}
			}
			if(yellow>3) {
				possibleMove.add(new Move("cureDisease","Yellow"));
			}
			if(black>3) {
				possibleMove.add(new Move("cureDisease","Black"));
			}
			if(red>3) {
				possibleMove.add(new Move("cureDisease","Red"));
			}
			if(blue>3) {
				possibleMove.add(new Move("cureDisease","Blue"));
			}
			
			if(b.getRoleOf(player)=="Scientist") {
				if(yellow==3) {
					possibleMove.add(new Move("cureDisease","Yellow"));
				}
				if(black==3) {
					possibleMove.add(new Move("cureDisease","Black"));
				}
				if(red==3) {
					possibleMove.add(new Move("cureDisease","Red"));
				}
				if(blue==3) {
					possibleMove.add(new Move("cureDisease","Blue"));
				}
			}
		}
		
    	return possibleMove;
    }
    
    /*** Sampling without replacement of the other agents past actions
     * 
     * @param otherAgentsActions:ArrayList of all the actions of other agents
     * @param m:Number of samples
     * @return ArrayList of size m,consisting of arbitrary other agents past actions
     */
    public static ArrayList<Action> sampleOtherAgentsActions(ArrayList<Action> otherAgentsActions,int m){
    	Random rand = new Random();
    	ArrayList<Action> sampled=new ArrayList<Action>();
    	for(int i=0;i<m;i++) {
    		sampled.add(otherAgentsActions.get(rand.nextInt(otherAgentsActions.size())));
    	}
    	return sampled;
    }
    
    
    public static double jointActionUtility(Action a,Action b) {
    	double utility=0;
    	Board afterActionA=a.getBoard(4);
    	Board afterActionB=b.getState().getBoard();
    	
    	//get the relevant distance after performing actions a and b respectively
    	int d=distanceBetween(afterActionA.getPawnsLocations(a.getPlayer()),afterActionB.getPawnsLocations(b.getPlayer()),afterActionA,a.getPlayer());
    	
    	//RULE #1
    	if(d<=2) {
    		utility=utility-1;
    	}
    	//RULE #2
    	if(d<=2 && !b.hasTreatDiseaseMove()) {
    		if(a.hasTreatDiseaseMove()) {
    			utility=utility+2;
    		}else {
    			if(afterActionB.getRoleOf(b.getPlayer())!="Quarantine Specialist") {
    				utility=utility+0.5;
    			}
    		}
    	}
    	
    	//RULE #3
    	if(d<=2 && b.hasBuildRSMove()) {
    		if(a.hasBuildRSMove()) {
    			utility=utility-1.5;
    		}
    	}
    	
    	//RULE #4
    	if(d<=2 && b.hasTreatDiseaseMove()) {
    		if(a.hasTreatDiseaseMove()) {
    			utility=utility-1;
    		}
    	}
    	
    	//RULE #5
    	int cardsOfColor=0;
    	for(String color:afterActionB.getHandOf(b.getPlayer())) {
    		if(afterActionB.searchForCity(color).getColour()==afterActionB.searchForCity(afterActionB.getPawnsLocations(a.getPlayer())).getColour()) {
    			cardsOfColor++;
    		}
    	}
    	if(cardsOfColor>=2) {
    		if(a.hasDirectFlightMove()||a.hasCharterFlightMove()||a.hasBuildRSMove()) {
    			utility=utility+0.9;
    		}
    	}
    	//System.out.println("JOIN UTILITY OF "+a.getPlayer()+" MOVE "+a.getMoveMessage(3)+" PLAYER "+b.getPlayer()+" "+b.getMoveMessage(3));
    	return utility;
    }
    
    /*** Measure the distance of two agents.Distance is the number of moves an agent has to make,to reach player's b location 
     * 
     * @param a,city where agent a is
     * @param b,city where agent b is
     * @return Number of moves an agent has to make,if he starts from the city a,to go to the 
     * city b
     */
    public static int distanceBetween(String a,String b,Board brd,int player) {
    	int dist=100;
    	City baseA=brd.searchForCity(a);
    	for(int i=0;i<baseA.getNeighboursNumber();i++) {
    		if(baseA.getNeighbour(i)==b)
    			return 1;
    		City baseB=brd.searchForCity(baseA.getNeighbour(i));
    		for(int j=0;j<baseB.getNeighboursNumber();j++) {
    			if(baseB.getNeighbour(i)==b)
    				return 2;
    		}
    	}
    	//distance for a direct flight 
    	for(String ct:brd.getHandOf(player)) {
    		if(ct==b)
    			return 1;
    		City baseC=brd.searchForCity(ct);
    		for(int i=0;i<baseC.getNeighboursNumber();i++) {
    			if(baseC.getNeighbour(i)==b)
    				return 2;
    		}
    	}
    	
    	//distance for shuttle flight 
    	int isInRS=0;
    	for(String s: brd.getRSLocations()) {
    		if(a==s) {
    			isInRS=1;
    		}		
    	}
    	if(isInRS==1) {
    		for(String s2:brd.getRSLocations()) {
    			if(s2==b) {
    				return 1;
    			}
    		}
    			
    	}
    	return dist;
    }
    
    /***
     * Decoding other players recommendations and store them to adaptive play matrix
     * @param recommendation,the String of the agent's suggestion
     * @param player,the player who sends the suggestion
     * @param curBoard,the board received after the agents talking
     * @return A string in suitable for the agent format
     */
    public static Action decodePlayerRecommendations(String recommendation,int player,Board curBoard) {
    	int move=0;
    	Action act=new Action(player,new State(curBoard));
    	Move opponentMoves[]=new Move[4];
    	for(String opMove:recommendation.split("#")) {
    		String moveType=opMove.substring(0,2);
    		if(moveType=="DT") {
    			player=Integer.parseInt(moveType.split(",")[0]);
    			opponentMoves[move]=new Move("driveTo",opMove.split(",")[2]);		
    		}
    		if(moveType=="DF") {
    			player=Integer.parseInt(moveType.split(",")[0]);
    			opponentMoves[move]=new Move("directFlight",opMove.split(",")[2]);		
    		}
    		if(moveType=="CF") {
    			player=Integer.parseInt(moveType.split(",")[0]);
    			opponentMoves[move]=new Move("charterFlight",opMove.split(",")[2]);		
    		}
    		if(moveType=="SF") {
    			player=Integer.parseInt(moveType.split(",")[0]);
    			opponentMoves[move]=new Move("shuttleFlight",opMove.split(",")[2]);		
    		}
    		if(moveType=="BRS") {
    			player=Integer.parseInt(moveType.split(",")[0]);
    			opponentMoves[move]=new Move("buildRS",opMove.split(",")[2]);		
    		}
    		if(moveType=="TD") {
    			player=Integer.parseInt(moveType.split(",")[0]);
    			opponentMoves[move]=new Move("treatDisease",opMove.split(",")[3]);		
    		}
    		if(moveType=="CD") {
    			player=Integer.parseInt(moveType.split(",")[0]);
    			opponentMoves[move]=new Move("cureDisease",opMove.split(",")[2]);		
    		}
    		move++;
    	}
    	act.setMoves(opponentMoves[0], opponentMoves[1], opponentMoves[2], opponentMoves[3]);
    	return act;
    }
    
    /*** Makes and returns an arraylist of all possible legal actions,for curPlayer at curBoard
     * 
     * @param curBoard
     * @param s
     * @param curPlayer
     * @return ArrayList of legal actions
     */
    public static ArrayList<Action> expandGame(Board curBoard,State s,int curPlayer){
    	ArrayList<Action> actionsAtState = new ArrayList<Action>();
    	for(Move move1:nextMove(curBoard,curPlayer)) {
    		Board b = matrixGame(curBoard,move1,curPlayer);
    		
    		for(Move move2:nextMove(b,curPlayer)) {
    			Board b2=matrixGame(b,move2,curPlayer);
    			if(move1.getTypeOfMove()=="driveTo" && curBoard.getPawnsLocations(curPlayer)==b2.getPawnsLocations(curPlayer)) {
    				continue;  //eliminate useless moving of type city1--->city2--->city1
    			}
    			
    			for(Move move3:nextMove(b2,curPlayer)) {
    				Board b3=matrixGame(b2,move3,curPlayer);
    				// Elimination of possible actions,according to the heuristics that approximate the Q
    				if(move1.getTypeOfMove()=="driveTo" && move2.getTypeOfMove()=="driveTo" && b3.searchForCity(b3.getPawnsLocations(curPlayer)).getMaxCube()==0) {
    					continue;
    				}
    				if(move1.getTypeOfMove()=="driveTo" && (b3.getPawnsLocations(curPlayer)==b.getPawnsLocations(curPlayer))) {
    					continue;
    				}
    				
    	    		for(Move move4:nextMove(b3,curPlayer)) {
    	    			Action q1 = new Action(curPlayer,s);
    	    			q1.setMoves(move1,move2,move3,move4);
    	    			q1.setBoards(b, b2, b3, matrixGame(b3,move4,curPlayer));
    	    			actionsAtState.add(q1);
    	    			
    	    		}
    	    	
    			}
    		}
    		
    	}
    	Collections.shuffle(actionsAtState);
    	return actionsAtState.size()>800?new ArrayList<Action>(actionsAtState.subList(0, 800)):actionsAtState;
    }
    
    // Expand a state of four moves
    public static Board matrixGame(Board b,Move m,int player) {
    	Board tmp = copyBoard(b);
    	if(m.getTypeOfMove()=="driveTo") {
    		tmp.setPawnsLocations(player, m.getObjective());
    	}
    	if(m.getTypeOfMove()=="directFlight") {
    		tmp.getHandOf(player).remove(m.getObjective());
    		tmp.setPawnsLocations(player, m.getObjective());
    	}
    	if(m.getTypeOfMove()=="charterFlight") {
    		tmp.getHandOf(player).remove(tmp.getPawnsLocations(player));
    		tmp.setPawnsLocations(player, m.getObjective());
    	}
    	if(m.getTypeOfMove()=="shuttleFlight") {
    		tmp.setPawnsLocations(player, m.getObjective());
    	}
    	if(m.getTypeOfMove()=="buildRS") {
    		tmp.buildRS(player, m.getObjective());
    	}
    	if(m.getTypeOfMove()=="treatDisease") {
    		tmp.treatDisease(player, tmp.getPawnsLocations(player), m.getObjective());
    	}
    	if(m.getTypeOfMove()=="cureDisease") {
    		tmp.cureDisease(player, m.getObjective());
    	}
    	return tmp;
    }
    
    
    // State of the game
    private static class State{
    	private Board board;
    	private double winnerProp;// V(s)
    	
    	private State(Board b) {
    		this.board=copyBoard(b);
    	}
		private Board getBoard() {
			return copyBoard(board);
		}

		private void setBoard(Board b) {
			this.board = copyBoard(b);
		}
		private double getWinnerProp() {
			return winnerProp;
		}

		private void setWinnerProp(double winnerProp) {
			this.winnerProp = winnerProp;
		}
    	
    }
    
    // move at some point
    private static class Move{
    	private String typeOfMove;
    	private String objective;
    	public Move(String type,String obj) {
    		this.typeOfMove=type;
    		this.objective=obj;
    	}
		private String getTypeOfMove() {
			return typeOfMove;
		}
		private void setTypeOfMove(String typeOfMove) {
			this.typeOfMove = typeOfMove;
		}
		private String getObjective() {
			return objective;
		}
		private void setObjective(String objective) {
			this.objective = objective;
		}
    	
    }
    
    
    
    private static class Action{
    	private int player;
    	private State s;
    	private double R;
    	private double Q;
    	private int moves;
    	private Move move1;
    	private Move move2;
    	private Move move3;
    	private Move move4; 
    	private Board board1;// Board after playing the move
    	private Board board2;
    	private Board board3;
    	private Board board4;
    	
    	public Action(int player,State s) {
    		this.player=player;
    		this.s=s;
    		s.setBoard(s.getBoard());
    		this.R=0;
    	}
    	//default constructor
    	public Action() {
    		
    	}
    	
    	public void heuristicReward(Board curBoard,double a) {
    		double Ra=0;
    		Board tmp=copyBoard(curBoard);
    		for(int i=0;i<4;i++) {
    			Ra=R+heuristicRewardOneMove(getMove(i+1).getTypeOfMove(),getMove(i+1).getObjective(),tmp,i+1);
    			tmp=copyBoard(getBoard(i+1));
    		}
    		R=a*Ra;
    	}
    	
    	public double heuristicRewardOneMove(String suggestions,String objective,Board b,int moveNo) {
    		if(moves<4) {
    			double Rh=0;
    			if(suggestions=="driveTo" || suggestions=="directFlight" || suggestions=="charterFlight" || suggestions=="directFlight") {
    				int cubes=0,cubesNeighbors_1_step=0,cubesNeighbors_2_steps=0;
    				cubes=b.searchForCity(b.getPawnsLocations(player)).getMaxCube();
    				City currentCity=b.searchForCity(b.getPawnsLocations(player));
    				for (int i = 0 ; i < currentCity.getNeighboursNumber() ; i++) {
    					City citytmp=b.searchForCity(currentCity.getNeighbour(i));
    				    cubesNeighbors_1_step=cubesNeighbors_1_step+citytmp.getMaxCube();
    				    for(int j=0 ; j<citytmp.getNeighboursNumber();j++) {
    				    	cubesNeighbors_2_steps=cubesNeighbors_2_steps+b.searchForCity(citytmp.getNeighbour(j)).getMaxCube();
    				    }
    				}
    		
    	    		if(b.getRoleOf(player)=="Medic") {
    	    			Rh=1.4*cubes+0.7*cubesNeighbors_1_step+0.2*cubesNeighbors_2_steps;
    	    		}else {
    	    			if(b.getRoleOf(player)=="Quarantine Specialist") {
    	    				Rh=3*cubes+3*cubesNeighbors_1_step+1*cubesNeighbors_2_steps;
    	    			}else {
    	    				Rh=cubes+0.4*cubesNeighbors_1_step+0.1*cubesNeighbors_2_steps;
    	    			}
    	    		}
    	    	}
    	    	if(suggestions=="directFlight") {
    	    		Rh=Rh-1;
    	    		if(b.getRoleOf(player)=="Scientist") {
    	    			Rh=Rh-2;
    	    		}
    	    		for(int j=1;j<5;j++) {
    	    			if(j==moveNo) {
    	    				continue;
    	    			}
    	    			if(getMove(j).getTypeOfMove()=="charterFlight"||getMove(j).getTypeOfMove()=="directFlight") {
    	    				Rh=Rh-8;
    	    			}
    	    		}
    	    		
    	    		
    	    	}
    	    	if(suggestions=="charterFlight") {
    	    		Rh=Rh-2;
    	    		if(b.getRoleOf(player)=="Scientist") {
    	    			Rh=Rh-0.4;
    	    		}
    	    		for(int j=1;j<5;j++) {
    	    			if(j==moveNo) {
    	    				continue;
    	    			}
    	    			if(getMove(j).getTypeOfMove()=="charterFlight"||getMove(j).getTypeOfMove()=="directFlight") {
    	    				Rh=Rh-10;
    	    			}
    	    		}
    	    	}
    	    	
    	    	if(suggestions=="shuttleFlight") {
    	    		
    	    		for(int j=1;j<5;j++) {
    	    			if(j==moveNo) {
    	    				continue;
    	    			}
    	    			if(getMove(j).getTypeOfMove()=="charterFlight"||getMove(j).getTypeOfMove()=="shuttleFlight") {
    	    				Rh=Rh-7;
    	    			}
    	    		}
    	    	}
    	    	
    	    	if(suggestions=="buildRS") {
    	    		if(b.getRoleOf(player)=="Operation Expert") {
    	    			Rh=Rh+4;
    	    		}else {
    	    			Rh=Rh+2;
    	    		}
    	    	}
    	    	if(suggestions=="treatDisease") {
    	    	
    	    		if(b.searchForCity(b.getPawnsLocations(player)).getMaxCube()==3) {
    	    			Rh=Rh+5;
    	    		}
    	    		
    	    		if(b.getRoleOf(player)=="Medic") {
    	    			Rh=Rh+3;
    	    		}else {
    	    			Rh=Rh+1;
    	    		}
    	    		
    	    		for(int j=0;j<4;j++) {
    	    		if(b.getCubesLeft(j)<4) {
    	    			Rh=Rh+8;
    	    		}
    	    	}
    	    	}
    	    	if(suggestions=="cureDisease") {
    	    		if(b.getRoleOf(player)=="Scientist") {
    	    			Rh=Rh+8;
    	    		}else {
    	    			Rh=Rh+5;
    	    		}
    	    	}
    	    	moves++;
    	    	return Rh;
    		}else {
    			System.out.println("Agent has already made four moves.");
    			return 0;
    		}
    	}
    	
    	public void adaptivePlayR(double weight,ArrayList<Action> otherAgentsActions,int m) {
    		Collections.shuffle(otherAgentsActions);
    		if(otherAgentsActions.size()==0) {
    			return;
    		}
    		ArrayList<Action> sampledActions=sampleOtherAgentsActions(otherAgentsActions,m);
    		double util=0;
    		for(Action a:sampledActions) {
    			util=util+jointActionUtility(this,a)*(1/m);
    		}
    		R=R+weight*util;
    	}
    	
    	public void beliefStateQ(double discount_factor,int round,AgentActionProbability a1,AgentActionProbability a2,AgentActionProbability a3,Board b) {
    		if(round==3) {
    			Q=R;
    			return;
    		}
    		double prop=0;
    		int nextIndex=0;
    		
    		Action next=new Action(player,new State(b));
    		if(round==0) {
    			next=a1.mostLikelyAction(b,next); // We pick one of four most possible actions
    		}
    		if(round==1) {
    			next=a2.mostLikelyAction(b,next);
    		}
    		if(round==2) {
    			next=a3.mostLikelyAction(b,next);
    		}
    		
    		next.beliefStateQ(discount_factor*discount_factor, round+1, a1, a2, a3, b);
    		Q=R+discount_factor*next.getQ();
    	}
    	
    	public Move getMove(int round) {
    		if(round==1)
    			return move1;
    		if(round==2)
    			return move2;
    		if(round==3)
    			return move3;
    		if(round==4)
    			return move4;
    		else
    			return null;
    	}
    	public Board getBoard(int round) {
    		if(round==1)
    			return board1;
    		if(round==2)
    			return board2;
    		if(round==3)
    			return board3;
    		if(round==4)
    			return board4;
    		else
    			return null;
    	}
    	public void setMoves(Move m1,Move m2,Move m3,Move m4) {
    		this.move1=m1;
    		this.move2=m2;
    		this.move3=m3;
    		this.move4=m4;
    	}
    	public void setBoards(Board b1,Board b2,Board b3,Board b4) {
    		this.board1=b1;
    		this.board2=b2;
    		this.board3=b3;
    		this.board4=b4;
    	}
    	public String getMoveMessage(int round) {
    		Move tmp=getMove(round);
    		if(tmp.getTypeOfMove()=="driveTo") {
    			return toTextDriveTo(player,tmp.getObjective());
    		}
    		if(tmp.getTypeOfMove()=="directFlight") {
    			return toTextDirectFlight(player,tmp.getObjective());
    		}
    		if(tmp.getTypeOfMove()=="charterFlight") {
    			return toTextCharterFlight(player,tmp.getObjective());
    		}
    		if(tmp.getTypeOfMove()=="shuttleFlight") {
    			return toTextShuttleFlight(player,tmp.getObjective());
    		}
    		if(tmp.getTypeOfMove()=="buildRS") {
    			return toTextBuildRS(player,tmp.getObjective());
    		}
    		if(tmp.getTypeOfMove()=="treatDisease") {
    			return toTextTreatDisease(player,getBoard(4).getPawnsLocations(player),tmp.getObjective());
    		}
    		if(tmp.getTypeOfMove()=="cureDisease") {
    			return toTextCureDisease(player,tmp.getObjective());
    		}
    		return null;
    	}
    	
    	public double getR() {
    		return R;
    	}
    	public double getQ() {
    		return Q;
    	}
    	public int getPlayer() {
    		return player;
    	}
    	public boolean hasDirectFlightMove() {
    		if(move1.getTypeOfMove()=="directFlight"||move2.getTypeOfMove()=="directFlight"||move3.getTypeOfMove()=="directFlight"||move4.getTypeOfMove()=="directFlight") {
    			return true;
    		}
    		return false;
    	}
    	public boolean hasCharterFlightMove() {
    		if(move1.getTypeOfMove()=="charterFlight"||move2.getTypeOfMove()=="charterFlight"||move3.getTypeOfMove()=="charterFlight"||move4.getTypeOfMove()=="charterFlight") {
    			return true;
    		}
    		return false;
    	}
    	public boolean hasTreatDiseaseMove() {
    		if(move1.getTypeOfMove()=="treatDisease"||move2.getTypeOfMove()=="treatDisease"||move3.getTypeOfMove()=="treatDisease"||move4.getTypeOfMove()=="treatDisease") {
    			return true;
    		}
    		return false;
    	}
    	public boolean hasBuildRSMove() {
    		if(move1.getTypeOfMove()=="buildRS"||move2.getTypeOfMove()=="buildRS"||move3.getTypeOfMove()=="buildRS"||move4.getTypeOfMove()=="buildRS") {
    			return true;
    		}
    		return false;
    	}
    	public State getState() {
    		return s;
    	}
    	public void setPlayer(int player) {
    		this.player=player;
    	}
    }
    
    /*** The possible states of the random variable Xi in the Bayessian network
     *   There are 7 possible states,with some probability each.
     *   
     *
     */
    private static class RandomVariable{
    	private double drive;
    	private double direct;
    	private double charter;
    	private double shuttle;
    	private double buildRS;
    	private double treat;
    	private double cure;
    	private double[] probabilities;
    	
    	public RandomVariable(double[] priors) {
    		if(priors.length!=7) {
    			System.out.println("Wrong initialization of random variable");
    		}
    		this.drive=priors[0];
    		this.direct=priors[1];
    		this.charter=priors[2];
    		this.shuttle=priors[3];
    		this.buildRS=priors[4];
    		this.treat=priors[5];
    		this.treat=priors[6];
    		this.probabilities=priors;
    		
    	}
    	//Return the move w.r.t their probabilities
    	public Move getMove(int player,Board b) {
    		Random r=new Random();
    		double d=r.nextDouble();
    		double accumulateProp=0;
    		int indexOfMove=0;
    		for(int i=0;i<this.probabilities.length;i++) {
    			accumulateProp=accumulateProp+this.probabilities[i];
    			if(accumulateProp>d) {
    				indexOfMove=i;
    				break;
    			}
    		}
    		
    		switch(indexOfMove) {
    		case 0: {
    			return new Move("driveTo",b.searchForCity(b.getPawnsLocations(player)).getNeighbour(0));
    		}
    		case 1:{
    			return new Move("directFlight",b.getHandOf(player).get(0));
    		}
    		case 2:{
    			return new Move("charterFlight",b.searchForCity(r.nextInt(b.getCitiesCount())).getName());
    		}
    		case 3:{
    			return new Move("shuttleFlight",b.getRSLocations().get(r.nextInt(b.getResearchStationsBuild())));
    		}
    		case 4:{
    			return new Move("buildRS",b.getPawnsLocations(player));
    		}
    		case 5:{
    			return new Move("treatDisease",b.getPawnsLocations(player));
    		}
    		case 6:{
    			return new Move("cureDisease",b.searchForCity(b.getPawnsLocations(player)).getColour());
    		}
    		default:{
    			return null;
    		}
    		}
    	}
    	
    	public void updateRV(Move latestMove) {
    		if(latestMove.getTypeOfMove()=="driveTo") {
    			drive++;
    			this.probabilities[0]=this.probabilities[0]+1;
    		}
    		if(latestMove.getTypeOfMove()=="directFlight") {
    			direct++;
    			this.probabilities[1]=this.probabilities[1]+1;
    		}
    		if(latestMove.getTypeOfMove()=="charterFlight") {
    			charter++;
    			this.probabilities[2]=this.probabilities[2]+1;
    		}
    		if(latestMove.getTypeOfMove()=="shuttleFlight") {
    			shuttle++;
    			this.probabilities[3]=this.probabilities[3]+1;
    		}
    		if(latestMove.getTypeOfMove()=="buildRS") {
    			buildRS++;
    			this.probabilities[3]=this.probabilities[3]+1;
    		}
    		if(latestMove.getTypeOfMove()=="treatDisease") {
    			treat++;
    			this.probabilities[3]=this.probabilities[3]+1;
    		}
    		if(latestMove.getTypeOfMove()=="cureDisease") {
    			cure++;
    			this.probabilities[3]=this.probabilities[3]+1;
    		}
    		
    		for(int i=0;i<7;i++) {
    			this.probabilities[i]=this.probabilities[i]/2;
    		}
    	}
    }
    
    /*** The Bayessian network for each agent
     * 
     *
     *
     */
    private static class AgentActionProbability{
    	private RandomVariable X1;
    	private RandomVariable X2;
    	private RandomVariable X3;
    	private RandomVariable X4;
    	private int player;
    	private String role;
    	
    	public AgentActionProbability(int player,String role) {
    		this.player=player;
    		this.role=role;
    		System.out.println("Player   "+player+"  role  "+role);
    		if(role=="Medic") {
    			double priors[]={0.09,0.09,0.09,0.09,0.05,0.50,0.09};
    			initiateRV(priors);
    		}
    		if(role=="Operations Expert") {
    			double priors[]= {0.09,0.05,0.18,0.09,0.50,0.05,0.04};
    			initiateRV(priors);
    		}
    		if(role=="Quarantine Specialist") {
    			double priors[]= {0.50,0.09,0.09,0.09,0.09,0.09,0.05};
    			initiateRV(priors);
    		}
    		if(role=="Scientist") {
    			double priors[]= {0.12,0.12,0.12,0.12,0.12,0.12,0.28};
    			initiateRV(priors);
    		}
    	}
    	
    	public void initiateRV(double[] priors) {
    		this.X1=new RandomVariable(priors);
			this.X2=new RandomVariable(priors);
			this.X3=new RandomVariable(priors);
			this.X4=new RandomVariable(priors);
    	}
    	
    	public Move movePicked(int orderInAction,Board b) {
    		if(orderInAction==0) {
    			return X1.getMove(player,b);
    		}
    		if(orderInAction==1) {
    			return X2.getMove(player,b);
    		}
    		if(orderInAction==2) {
    			return X3.getMove(player,b);
    		}
    		if(orderInAction==3) {
    			return X4.getMove(player,b);
    		}
    		return null;
    	}
    	
    	public Action mostLikelyAction(Board b,Action a) {
    		a.setMoves(movePicked(0,b), movePicked(1,b), movePicked(2,b), movePicked(3,b));
    		return a;
    	}
    	
    	public void considerPosteriors(Action latestAction) {
    		this.X1.updateRV(latestAction.getMove(1));
    		this.X2.updateRV(latestAction.getMove(2));
    		this.X3.updateRV(latestAction.getMove(3));
    		this.X4.updateRV(latestAction.getMove(4));
    	}
    }
}
 
