package Subway;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.alibaba.fastjson.*;

public class Subway {
	public static String rfileName;
	public static String wfileName;
	public static String lineName;
	public static String startStation;
	public static String endStation;
	
	public static Map<String, List<String>> lineInfo = new HashMap<String, List<String>>();
	public static Map<String, List<String>> stationInfo = new HashMap<String, List<String>>();
	public static Map<String, Integer> staNum = new HashMap<String, Integer>();
	public static String[] stations = new String[10000];
	
	public static List<Integer>[] neighbors = new List[500];   // graph
	
	static void initJsonData() {
		String path = FileReadWrite.getPath(rfileName);
		String jsonString = FileReadWrite.readJsonData(path);
		try {
			JSONArray lineArr = JSON.parseArray(jsonString);
			int cnt = 0;
			System.out.print("地铁信息加载成功：");
			for(int i = 0; i < lineArr.size(); i++) {
				JSONObject jsonObj = lineArr.getJSONObject(i);
				JSONArray stationArr =  jsonObj.getJSONArray("Station");
				List<String> l = new ArrayList<>();
				System.out.print( "\n" + jsonObj.getString("Line") + " : ");
				for(int j = 0; j < stationArr.size(); j++) {
					String tmpSta = stationArr.getString(j); 
					System.out.print(tmpSta + ' ');
					l.add(tmpSta);
					if(stationInfo.containsKey(tmpSta)) {
						stationInfo.get(tmpSta).add(jsonObj.getString("Line"));
					}else {
						List<String> t = new ArrayList<>();
						t.add(jsonObj.getString("Line"));
						stationInfo.put(tmpSta, t);
						neighbors[cnt] = new ArrayList<Integer>();
						stations[cnt] = tmpSta;
						staNum.put(tmpSta, cnt++);
					}
					if(j > 0) {
						String preSta = stationArr.getString(j-1);
						int v = staNum.get(preSta);
						int u = staNum.get(tmpSta);
						neighbors[v].add(u);
						neighbors[u].add(v);
					}
				}
				lineInfo.put(jsonObj.getString("Line"),  l);
			}		
			System.out.println();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static void queryLine(boolean f) {
		String info;
		if(lineInfo.containsKey(lineName)) {
			info = lineName + " : " + lineInfo.get(lineName).toString();
		}else {
			info = "该站点不存在\n";
		}
		if(f) {
			String path = FileReadWrite.getPath(wfileName);
			FileReadWrite.write2File(path, info);
		}
		System.out.println("地铁线路信息查询结果：\n" + info);
	}
	
	public static boolean needTransfer(String sta1, String sta2) {
			for(String it2 : stationInfo.get(sta2)) {
				if(sta1.equals(it2)) {
					return false;
				}
			}
		return true;
	}
	
	public static void queryShortestLine(boolean f) {
		boolean[] isVisited = new boolean[500];	
		int[] father = new int[500];
		for(int i = 0; i < 500; i++) {
			father[i] = -1;
			isVisited[i]  = false;
		}
		List<Integer> searchOrder = new ArrayList<>();
		LinkedList<Integer> queue = new LinkedList<>();
		int v = staNum.get(startStation);
		int e = staNum.get(endStation);
		queue.offer(v);
		isVisited[v] = true;
		
		while(!queue.isEmpty()) {
			int u = queue.poll();
			searchOrder.add(u);
			for(int it : neighbors[u]) {
				if(!isVisited[it]) {
					queue.offer(it);
					father[it] = u;
					isVisited[it] = true;
				}
				if(isVisited[e])	break;
			}
			if(isVisited[e])	break;
		}
		
		List<Integer> path = new ArrayList<Integer>();
		for(int i = e; i != v; i = father[i]) {
			path.add(i);
		}
		path.add(v);
		Collections.reverse(path);
		
		String info = Integer.toString(path.size()) + "\r\n";
		String curLine = stationInfo.get(stations[path.get(0)]).get(0);
		for(int i = 1; i < path.size(); i++) {
			info = info + stations[path.get(i-1)] + "\r\n";
			if(needTransfer(curLine, stations[path.get(i)])) {
				curLine = stationInfo.get(stations[path.get(i)]).get(0);
				info = info + curLine + "\r\n";
			}
		}
		info = info + stations[path.get(path.size()-1)] + "\r\n";
		
		String fpath = FileReadWrite.getPath(wfileName);
		FileReadWrite.write2File(fpath, info);
	}
	
	public static void main(String[] args) {
		boolean f1 = false, f2 = false, f3 = false, f4 = false;
		for(int i = 0; i < args.length; i++) {
			if(args[i].equals("-map")) {
				rfileName = args[++i];
				f3 = true;
			}
			else if(args[i].equals("-a")) {
				lineName = args[++i];
				f1 = true;
			}
			else if(args[i].equals("-o")) {
				wfileName = args[++i];
				f4 = true;
			}
			else if(args[i].equals("-b")) {
				f2 = true;
				startStation = args[++i];
				endStation = args[++i];
			}
		}	
		if(f3) {
			initJsonData();
			if(f1) {
				queryLine(f4);
			}else if(f2) {
				queryShortestLine(f4);
			}
		}
	}
}
