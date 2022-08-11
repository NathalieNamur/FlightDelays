package it.polito.tdp.extflightdelays.db;

import java.util.HashMap;
import java.util.Map;

import it.polito.tdp.extflightdelays.model.Airport;

public class TestDAO {

	public static void main(String[] args) {

		ExtFlightDelaysDAO dao = new ExtFlightDelaysDAO();
		
		
		Map<Integer,Airport> idMap = new HashMap<>();
		dao.loadAllAirports(idMap);
		
		System.out.println("Numero aeroporti: "+idMap.size());
		
		
		System.out.println("Numero vertici (x=10): "+dao.getVertici(10, idMap).size());
		
		
		System.out.println("Numero rotte: "+dao.getRotte(idMap).size());
		
	}

}
