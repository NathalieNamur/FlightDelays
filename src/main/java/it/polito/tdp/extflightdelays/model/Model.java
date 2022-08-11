package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	
	private ExtFlightDelaysDAO dao;
	private Map<Integer,Airport> idMap; 
	private Graph<Airport,DefaultWeightedEdge> grafo; 
	
	
	
	public Model() {
		
		dao = new ExtFlightDelaysDAO();
		
		idMap = new HashMap<>();
		dao.loadAllAirports(idMap);
	} 
	
	
	
	//METODO DI CREAZIONE E POPOLAMENTO GRAFO:
	public void creaGrafo(int x) {
		
		//inizializzazione grafo:
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		
		//popolamento vertici grafo (CON VINCOLO):
		Graphs.addAllVertices(grafo, dao.getVertici(x, idMap));
		
		
		//popolamento artici grafo:
		
		//poichè il grafo non è orientato, per attribuire il peso corretto 
		//a ciascun arco è necessario sommare il peso A->B e il peso B->A.
		
		//Per ogni rotta, viene considerato l'arco avente come vertici
		//di partenza e di arrivo quelli della rotta, controllando che 
		//tali vertici appartengano effettivamente al grafo.
		//Se l'arco non esiste ancora nel grafo, viene creato e aggiunto,
		//con il peso indicato nella rotta.
		//Se tale arco esiste già nel grafo, il suo peso deve essere aggiornato,
		//aggiungendo il peso indicato nella rotta.
		
		for (Rotta r : dao.getRotte(idMap)) {
		
			//controllo correttezza db!
			if(grafo.containsVertex(r.getA1()) && grafo.containsVertex(r.getA2())) {
				
				DefaultWeightedEdge edge = grafo.getEdge(r.getA1(),r.getA2());
				
				if(edge == null) 
					Graphs.addEdgeWithVertices(grafo, r.getA1(), r.getA2(), r.getnVoli());
				
				else {
					double pesoVecchio = grafo.getEdgeWeight(edge);
					double pesoNuovo = pesoVecchio + r.getnVoli();
					this.grafo.setEdgeWeight(edge, pesoNuovo);
				}
			
			}
		}
			
	}
	
	public int getNumVertici() {
		return grafo.vertexSet().size();
	}
	
	public int getNumArchi() {
		return grafo.edgeSet().size();
	}
	
	
	//METODO CHE RESTITUISCE LA LISTA DEI VERTICI DEL GRAFO:
	public List<Airport> getVertici(){
		
		//popolamento della lista tramite il set di vertici
		List<Airport> vertici = new ArrayList<>(grafo.vertexSet());
		
		//ordinamento della lista
		Collections.sort(vertici);
		
		return vertici;
	}
	
	
	//METODO CHE RESTITUISCE IL PERCORSO TRA GLI AEREOPORTI DATI ... :
	public List<Airport> calcolaPercorso (Airport a1, Airport a2){
				
		//Creazione lista di aeroporti corrispondenti al percorso:
		List<Airport> percorso = new ArrayList<>();
		 	
		
		//Iteratore in ampiezza:
		BreadthFirstIterator<Airport,DefaultWeightedEdge> it =
				new BreadthFirstIterator<>(grafo,a1);
				
		
		//Visita del grafo:
		 
		//... E VERIFICA CHE I DUE AEREOPORTI DATI SIANO 
		//EFFETTIVAMENTI COLLEGATI:
		 
		Boolean trovato = false;
		
		while(it.hasNext()) {
			
			Airport visitato = it.next();
			
			 if(visitato.equals(a2))
				 trovato = true;
		}
		
		
		//Percorso:
		
		if(trovato) {
			
			percorso.add(a2);
		
			Airport step = it.getParent(a2);
			while (!step.equals(a1)) {
				percorso.add(0,step);
				step = it.getParent(step);
			}
			 
			percorso.add(0,a1);
		
		
		 return percorso;
		
		} else 
			 return null; 
	}
	
}