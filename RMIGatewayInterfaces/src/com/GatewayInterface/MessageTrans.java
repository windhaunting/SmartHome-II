package com.GatewayInterface;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Stack;

public class MessageTrans implements Serializable {
	
	public static Stack<Integer> LogicClocks = new Stack<Integer>();
    public static HashMap<String,Integer> ClockVlaue = new HashMap<String,Integer>();          //for different event having different clock timestamp value

	public static int QueryStateResult = 0;
	public static String RecvEvent = "";

	
	public static void LamportLogicClock(Stack<Integer> SendLogicClock, Stack<Integer> RecvLogicClock, String event, HashMap<String,Integer> ClockOrigVlaue)
	{
		LogicClocks = RecvLogicClock;
		ClockVlaue = ClockOrigVlaue;
		int updateVal = Math.max(SendLogicClock.peek(), RecvLogicClock.peek())+1;
		
		ClockVlaue.put(event, updateVal);
		LogicClocks.push(updateVal);
		RecvEvent = event;
	}
	
	//wait timeinterval
	
		public static boolean waittimeInterval(int time)            //ms
		{
			long timeStart = System.currentTimeMillis();
			long timeEnd = 0;
			int intervalTime = 0;
			while(intervalTime <= time)
			{
				timeEnd = System.currentTimeMillis();
				intervalTime =(int)(timeEnd - timeStart);
			}
			return true;
		}
		
	    public static LinkedHashMap<Double, Integer> readLab2TestInput(int valuecolumn)
	    {
			String workingDirectory = System.getProperty("user.dir");
			String Lab2TestCaseFile = Const.LAB2_TEST_INPUT_FILE;
			Lab2TestCaseFile = workingDirectory.concat("/"+Lab2TestCaseFile);
			System.out.print(Lab2TestCaseFile);
			BufferedReader br = null;
			String line = "";
			String cvsSplitBy = ",";
			
			 LinkedHashMap<Double, Integer> EventHash = new LinkedHashMap<Double, Integer>();
			try {
		 
				br = new BufferedReader(new FileReader(Lab2TestCaseFile));
				int lineNumCount = 0;
				while ((line = br.readLine()) != null) {
				        // use comma as separator
					String[] lineInformation = line.split(cvsSplitBy);
					System.out.print(lineNumCount);
					++lineNumCount;
					if(1 >= lineNumCount)
					{
						continue;
					}
					double timestamp = Double.parseDouble(lineInformation[0]);
					int value = Integer.parseInt(lineInformation[valuecolumn]);
					EventHash.put(timestamp, value);
					System.out.println("lineInformation [code= " + lineInformation[0] 
		                                 + " , name=" + lineInformation[valuecolumn] + "]");
				}
		 
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return EventHash;
	    }
}
