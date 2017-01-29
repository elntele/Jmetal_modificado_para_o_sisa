package sisa;

import java.util.ArrayList;
import java.util.List;

public class SugestaoMatricula {
	private Long id;
	private int contadorAreaEnsiso;
	private int contadorAreaArq;
	private int contadorAreaFC;
	private List <Float> VarianciaPeriodo=new ArrayList<Float>();
	private float varianciaTotal;
	private int cargaHoraria;
	private float media=(float) 2.0; //levar para o codigo
	private List<Disciplina>  gradeDeHorarios= new ArrayList<Disciplina>(); // lista de disciplinas pois o obj disciplina tem um map com dia e hora.

	
	public SugestaoMatricula(int cargaHoraria, float media) {
		super();
		this.cargaHoraria = cargaHoraria;
		this.media = media;
	}

	public SugestaoMatricula() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	
	public List<Float> getVarianciaPeriodo() {
		return VarianciaPeriodo;
	}

	public void setVarianciaPeriodo(List<Float> varianciaPeriodo) {
		VarianciaPeriodo = varianciaPeriodo;
	}

	public float getVarianciaTotal() {
		return varianciaTotal;
	}

	public void setVarianciaTotal(float varianciaTotal) {
		this.varianciaTotal = varianciaTotal;
	}

	public int getCargaHoraria() {
		return cargaHoraria;
	}

	public void setCargaHoraria(int cargaHoraria) {
		this.cargaHoraria = cargaHoraria;
	}

	public List<Disciplina> getGradeDeHorarios() {
		return gradeDeHorarios;
	}

	public void setGradeDeHorarios(List<Disciplina> gradeDeHorarios) {
		this.gradeDeHorarios = gradeDeHorarios;
	}
	
	// variacia do periodo
	public void mediaVarianciaPeriodo(){
		float varia=0; 
		int cont=0, deivisorPorPeriodo=0;
		for(Disciplina D : gradeDeHorarios) {// percorre o array de disciplinas para calcular a variancia por periodo
			cont+=1;
			if (D.getNome()!="tampabrecha"){ // se não houver disciplina na posição
				deivisorPorPeriodo+=1;		// a estratégia é criar uma disciplina 
				}							// com esse nome	
			varia += (float) Math.pow( D.getGrauDificuldade()- this.media,2);
			if (cont%8==0){
				if (deivisorPorPeriodo!=0){	
				this.VarianciaPeriodo.add(varia/deivisorPorPeriodo);
				cont=0;
				 }else{this.VarianciaPeriodo.add((float) 0);}
			}
		}
		
	}
	
	//metodo pra contar quantas disciplinas de cada area
	public void contadorDeArea(){
		for(Disciplina D : gradeDeHorarios){
			switch (D.getArea().getNome()){
				case "ensiso": this.contadorAreaEnsiso+=1;
								break;
				case "Arq": this.contadorAreaArq+=1;
								break;
				case "FC": this.contadorAreaFC+=1;
								break;
				default: break;					
			}
		}	
	}
	
}