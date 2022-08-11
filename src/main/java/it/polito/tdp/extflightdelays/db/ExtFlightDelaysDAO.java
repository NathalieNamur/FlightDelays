package it.polito.tdp.extflightdelays.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.extflightdelays.model.Airline;
import it.polito.tdp.extflightdelays.model.Airport;
import it.polito.tdp.extflightdelays.model.Flight;
import it.polito.tdp.extflightdelays.model.Rotta;

public class ExtFlightDelaysDAO {

	//METODO PER POPOLARE L'idMap CON I DATI DEL DB:
	public void loadAllAirports(Map<Integer,Airport> idMap) {
		
		String sql = "SELECT * FROM airports";

		try {
			
			Connection conn = ConnectDB.getConnection();
			
			PreparedStatement st = conn.prepareStatement(sql);
			
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				
				if(!idMap.containsKey(rs.getInt("ID"))) {
					
					Airport a = new Airport(rs.getInt("ID"), rs.getString("IATA_CODE"), rs.getString("AIRPORT"),
											rs.getString("CITY"), rs.getString("STATE"), rs.getString("COUNTRY"), rs.getDouble("LATITUDE"),
											rs.getDouble("LONGITUDE"), rs.getDouble("TIMEZONE_OFFSET"));
					
					idMap.put(a.getId(), a);
				}

			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore nel metodo loadAllAirports().");
			throw new RuntimeException("Error Connection Database");
		}
	}

	
	//METODO CHE RESTITUISCE LA LISTA DEI VERTICI
	//(aeroporti del db che soddisfano il vincolo):
	public List<Airport> getVertici(int x, Map<Integer,Airport> idMap){
		
		String sql = "SELECT a.id "
				   + "FROM airports a, flights f "
				   + "WHERE (a.id = f.ORIGIN_AIRPORT_ID OR a.id = f.DESTINATION_AIRPORT_ID) "
				   + "GROUP BY a.id "
				   + "HAVING COUNT(DISTINCT(f.AIRLINE_ID)) >= ?";
		
		List<Airport> result = new ArrayList<Airport>();
		
		try {
			
			Connection conn = ConnectDB.getConnection();
			
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, x);
			
			ResultSet rs = st.executeQuery();
			
			while (rs.next()) {
				result.add(idMap.get(rs.getInt("id")));
			}
			
			st.close();
			conn.close();
			
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore nel metodo getVertici().");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	
	//METODO CHE RESTITUISCE LA LISTA DELLE ROTTE PRESENTI NELLA idMap:
	public List<Rotta> getRotte(Map<Integer, Airport> idMap) {
		
		String sql = "SELECT f.ORIGIN_AIRPORT_ID as a1, f.DESTINATION_AIRPORT_ID as a2, COUNT(*) AS n "
				   + "FROM flights f "
				   + "GROUP BY f.ORIGIN_AIRPORT_ID, f.DESTINATION_AIRPORT_ID";
		
		List<Rotta> result = new ArrayList<Rotta>();
		
		try {
			
			Connection conn = ConnectDB.getConnection();
			
			PreparedStatement st = conn.prepareStatement(sql);
			
			ResultSet rs = st.executeQuery();
			
			while(rs.next()) {
				
				Airport partenza = idMap.get(rs.getInt("a1"));
				Airport destinazione = idMap.get(rs.getInt("a2"));
				
				 //controllo correttezza db!
				//if(partenza != null && destinazione != null) 
					result.add(new Rotta(partenza, destinazione, rs.getInt("n")));
					
			}
			
			st.close();
			conn.close();
			
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore nel metodo getRotte().");
			throw new RuntimeException("Error Connection Database");
		}
		
	}
}
