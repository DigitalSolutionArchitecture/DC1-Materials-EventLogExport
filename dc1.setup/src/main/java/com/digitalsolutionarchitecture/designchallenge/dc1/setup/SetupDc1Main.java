package com.digitalsolutionarchitecture.designchallenge.dc1.setup;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SetupDc1Main {

	public static final List<String> LOCATION_PATHS = Arrays.asList(
		"/process",
		"/process/sequence",
		"/process/sequence/invoke",
		"/process/sequence/assign",
		"/process/sequence/reply",
		"/process/sequence/receive",
		"/process/sequence/sequence/receive",
		"/process/sequence/sequence/assign",
		"/process/sequence/if/assign"
	);
	private static final List<String> FAULT_NAMES = Arrays.asList(
		"Some Fault",
		"Another Fault",
		"Server Error"
	);
	
	public static void main(String[] args) throws IOException {
		String mysqlHost = fetchConfig(args, 0, "MySQL Host Name", false);
		String mysqlPort = fetchConfig(args, 1, "MySQL Port", false);
		String mysqlDatabase = fetchConfig(args, 2, "MySQL Database", false);
		String mysqlUser = fetchConfig(args, 3, "MySQL User", false);
		String mysqlPassword = fetchConfig(args, 4, "MySQL User Password", true);
		
		int processInstances = 1000;
		int eventsPerProcessInstance = 2000;
		
		System.out.println("Configuration");
		System.out.println("=============");
		System.out.println("MySQL Host:          " + mysqlHost);
		System.out.println("MySQL Database:      " + mysqlDatabase);
		System.out.println("MySQL Port:          " + mysqlPort);
		System.out.println("MySQL User:          " + mysqlUser);
		System.out.println("MySQL User Password: " + mysqlPassword);
		System.out.println("#Process Instances : " + processInstances);
		System.out.println("#Events/Instance   : " + eventsPerProcessInstance);
		System.out.println();
		
		System.out.print("Loading MySQL Driver... ");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("done");
		} catch (ClassNotFoundException e) {
			System.out.println("FAILED");
			e.printStackTrace();
			return;
		}
		
		System.out.print("Connecting to data base... ");
		try(
			Connection c = DriverManager.getConnection("jdbc:mysql://" + mysqlHost + ":" + mysqlPort + "/" + mysqlDatabase + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", mysqlUser, mysqlPassword);
			PreparedStatement s = c.prepareStatement("INSERT INTO tblProcessLogData VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			) {
			System.out.println("done");
			Random random = new Random(1);
			
			System.out.print("Creating fake process logs...");
			c.setAutoCommit(false);
			long start = System.currentTimeMillis();
			for(int p = 0; p < processInstances; p++) {
				s.setInt(1, p);
				s.setInt(3, random.nextInt(1000000));
				for(int i = 0; i < eventsPerProcessInstance; i++) {
					s.setInt(2, i);
					s.setString(4, LOCATION_PATHS.get(random.nextInt(LOCATION_PATHS.size())));
					s.setInt(5, random.nextInt(100000));
					s.setInt(6, random.nextInt(100000));
					s.setInt(7, random.nextInt(100000));
					s.setInt(8, random.nextInt(10));
					s.setInt(9, 0);
					s.setInt(10, 0);
					boolean hasFault = random.nextBoolean();
					s.setString(11, hasFault ? null : FAULT_NAMES.get(random.nextInt(FAULT_NAMES.size())));
					s.setString(12, hasFault ? null : FAULT_NAMES.get(random.nextInt(FAULT_NAMES.size())));
					if(hasFault) {
						s.setInt(13, random.nextInt(10));
					} else {
						s.setNull(13, Types.INTEGER);
					}
					s.setInt(14, Math.abs(random.nextInt()));
					s.setString(15, random.nextBoolean() ? "Some DataDocument" : null);
					
					s.execute();
				}
				c.commit();
				System.out.println((p+1) + " processes written in " + ((System.currentTimeMillis() - start) / 1000) + "s");
			}
			System.out.println(" done");
			
		} catch (SQLException e) {
			System.out.println("FAILED");
			e.printStackTrace();
		}
	}

	private static String fetchConfig(String[] args, int index, String prompt, boolean isPassword) {
		if(args.length > index) {
			return args[index];
		} else {
			if(!isPassword) {
				return System.console().readLine(prompt + ": ");
			} else {
				return new String(System.console().readPassword(prompt + ": "));
			}
		}
	}
	
}
