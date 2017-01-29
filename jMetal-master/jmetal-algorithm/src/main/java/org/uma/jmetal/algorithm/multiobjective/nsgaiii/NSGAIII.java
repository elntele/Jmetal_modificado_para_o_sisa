package org.uma.jmetal.algorithm.multiobjective.nsgaiii;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.util.EnvironmentalSelection;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.util.ReferencePoint;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;

import sisaJMetalMain.PreparacaoDoProblema;
import sisaJmetalbeans.Disciplina;

/**
 * Created by ajnebro on 30/10/14.
 * Modified by Juanjo on 13/11/14
 *
 * This implementation is based on the code of Tsung-Che Chiang
 * http://web.ntnu.edu.tw/~tcchiang/publications/nsga3cpp/nsga3cpp.htm
 */
@SuppressWarnings("serial")
public class NSGAIII<S extends Solution<?>> extends AbstractGeneticAlgorithm<S, List<S>> {
  protected int iterations ;
  protected int maxIterations ;
  protected int cont=1;

  protected SolutionListEvaluator<S> evaluator ;

  protected Vector<Integer> numberOfDivisions  ;
  protected List<ReferencePoint<S>> referencePoints = new Vector<>() ;
  
  
  /**
   * incluido por jorge candeias para os fins do projeto
   * sisa
   */
  protected PreparacaoDoProblema preparacao;
  protected List <Disciplina> DisciplinasObrigatorias= new ArrayList<>();
  protected List <Disciplina> Disciplinas =new ArrayList<>();
  private HashMap <String, Disciplina> disciplinaMap = new HashMap <String, Disciplina>();
  private HashMap <Integer,Disciplina> disciplinaMapByCod=new HashMap <Integer, Disciplina>();
  protected S frontDeSeguranca ;
 
  protected boolean frontDeSegurancaSetadado=false;
  
  public void MontaHasMap(){
	  for (Disciplina D : this.Disciplinas ) {
	     this.disciplinaMap.put(Integer.toString(D.getId()), D);
	  	}
	  for (Disciplina D : this.Disciplinas ) {
		     this.disciplinaMapByCod.put(D.getCodigo(), D);
		  	}
	  }

  
  public void montaDisciplinasObrigatorias(){
		for (Disciplina D:this.Disciplinas){			
			if(!D.getPeriodo().equals("0")){
				this.DisciplinasObrigatorias.add(D);
			}
		}
	
		List <Disciplina> copia = new ArrayList <>();
		copia.addAll(this.DisciplinasObrigatorias);
		// removendo as ja pagas pelo aluno da copia da lista de obrigatorias	
		  for (Disciplina D:this.preparacao.getAluno().getDiscPagas()){
			  
			  for (Disciplina DI:this.DisciplinasObrigatorias){
				  if (D.getId()==DI.getId()) copia.remove(D);
			  }
		  }
		this.DisciplinasObrigatorias=copia;
	}
  
  public void montadisciplinas(){
	  this.Disciplinas=this.preparacao.getDisciplinas();
  }

  
  
/** Constructor */
  public NSGAIII(NSGAIIIBuilder<S> builder) { // can be created from the NSGAIIIBuilder within the same package
    super(builder.getProblem()) ;
    maxIterations = builder.getMaxIterations() ;

    crossoverOperator =  builder.getCrossoverOperator() ;
    mutationOperator  =  builder.getMutationOperator() ;
    selectionOperator =  builder.getSelectionOperator() ;

    evaluator = builder.getEvaluator() ;
   
    /**
     * incluido por jorge candeias para os fins do projeto sisa
     */
    preparacao=builder.getPreparacao();
    montadisciplinas();
    montaDisciplinasObrigatorias();
    MontaHasMap();

    /// NSGAIII
    numberOfDivisions = new Vector<>(1) ;
    numberOfDivisions.add(12) ; // Default value for 3D problems

    (new ReferencePoint<S>()).generateReferencePoints(referencePoints,getProblem().getNumberOfObjectives() , numberOfDivisions);

    int populationSize = referencePoints.size();
 //   System.out.println(referencePoints.size());
    while (populationSize%4>0) {
      populationSize++;
    }

    setMaxPopulationSize(populationSize);

    JMetalLogger.logger.info("rpssize: " + referencePoints.size()); ;
  }

  @Override
  protected void initProgress() {
    iterations = 1 ;
  }

  @Override
  protected void updateProgress() {
    iterations++ ;
  }

  @Override
  protected boolean isStoppingConditionReached() {
    return iterations >= maxIterations;
  }

  @Override
  protected List<S> evaluatePopulation(List<S> population) {
    population = evaluator.evaluate(population, getProblem()) ;

    return population ;
  }

  @Override
  protected List<S> selection(List<S> population) {
    List<S> matingPopulation = new ArrayList<>(population.size()) ;
    for (int i = 0; i < getMaxPopulationSize(); i++) {
      S solution = selectionOperator.execute(population);
      matingPopulation.add(solution) ;
    }

    return matingPopulation;
  }

  @Override
  protected List<S> reproduction(List<S> population) {
    List<S> offspringPopulation = new ArrayList<>(getMaxPopulationSize());
    for (int i = 0; i < getMaxPopulationSize(); i+=2) {
      List<S> parents = new ArrayList<>(2);
      parents.add(population.get(i));
      parents.add(population.get(Math.min(i + 1, getMaxPopulationSize()-1)));

      List<S> offspring = crossoverOperator.execute(parents);

      mutationOperator.execute(offspring.get(0));
      mutationOperator.execute(offspring.get(1));

      offspringPopulation.add(offspring.get(0));
      offspringPopulation.add(offspring.get(1));
    }
    return offspringPopulation ;
  }

  
  private List<ReferencePoint<S>> getReferencePointsCopy() {
	  List<ReferencePoint<S>> copy = new ArrayList<>();
	  for (ReferencePoint<S> r : this.referencePoints) {
		  copy.add(new ReferencePoint<>(r));
	  }
	  return copy;
  }
  
  @Override
  protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
   
	List<S> jointPopulation = new ArrayList<>();
    jointPopulation.addAll(population) ;
    jointPopulation.addAll(offspringPopulation) ;

    Ranking<S> ranking = computeRanking(jointPopulation);
    
    //List<Solution> pop = crowdingDistanceSelection(ranking);
    List<S> pop = new ArrayList<>();
    List<List<S>> fronts = new ArrayList<>();
    int rankingIndex = 0;
    int candidateSolutions = 0;
    while (candidateSolutions < getMaxPopulationSize()) {
      fronts.add(ranking.getSubfront(rankingIndex));
      candidateSolutions += ranking.getSubfront(rankingIndex).size();
      if ((pop.size() + ranking.getSubfront(rankingIndex).size()) <= getMaxPopulationSize())
        addRankedSolutionsToPopulation(ranking, rankingIndex, pop);
      rankingIndex++;
    }
    
    // A copy of the reference list should be used as parameter of the environmental selection
    System.out.println("contador de iterações: "+this.cont);//jorge
    EnvironmentalSelection<S> selection =
            new EnvironmentalSelection<>(fronts,getMaxPopulationSize(),getReferencePointsCopy(),
                    getProblem().getNumberOfObjectives());
    pop = selection.execute(pop);
    this.cont+=1; // jorge
    return pop;
  }

  @Override
  public List<S> getResult() {
    return getNonDominatedSolutions(getPopulation()) ;
  }

  protected Ranking<S> computeRanking(List<S> solutionList) {
    Ranking<S> ranking = new DominanceRanking<>() ;
    ranking.computeRanking(solutionList) ;

    return ranking ;
  }
  
  /**
   * metodo para testar se a solucao nao 
   * esta fora da ordem de prérequisito
   */
  
  public boolean verifivaForaDeOrdemDePreRequisito(S Sugestao){
		//HashMap <Integer, Integer> ordemDeMatricula = new HashMap <Integer, Integer>();
		boolean ForaDaOrdem =false;
		Disciplina Dis =new Disciplina();
		//transforma sugestao em um array de string
		  List<S>copia= new ArrayList<>();
		  	copia.add(Sugestao);
				String[] L=copia.toString().split(" ");
				String tempo="";
				// descobre qtd de periodos da sugestão
				int w=0;
				for (String S: L){			
					if (S.equals("Objectives:")){
						tempo=L[w+1];
						break;
					}
					w+=1;
				}
				float temp=Float.parseFloat(tempo);
				int fim = (int)temp;

			//coloca todos os ids de disciplinas já pagas numa lista 
			List <Integer> listaPeriodosPassados = new ArrayList<> ();
			for (Disciplina D: this.preparacao.getAluno().getDiscPagas()){
				listaPeriodosPassados.add(D.getId());
				}
			
//			System.out.println("dispciplinas do passado: " +listaPeriodosPassados);
			
			// percorre a sugestao ate numero de periodos validos
			// contando de 8 em 8 disciplinas e comparando com as que estão na lista de 
			// ja pagas nos periodos passados
			List <Integer> listaPeriodo = new ArrayList<> ();
			for (int i=1; i<=(fim*8);i++){
				// percorrendo a lista de pre requisitos
				try {
					for (Integer pre:this.disciplinaMap.get(L[i]).getPreRequisitos()){
//						System.out.println("verificando pre requisitos de: "+L[i]);
//						System.out.println("pre requisito: " +this.disciplinaMapByCod.get(pre).getId());
						
						if (!listaPeriodosPassados.contains(this.disciplinaMapByCod.get(pre).getId())){// se a lista de disciplinas do passado não 
							ForaDaOrdem=true;						 // contem o pre requisitos para e retorna falso	
							break;
							
						}else {
//							System.out.println("a lista "+listaPeriodosPassados);
//							System.out.println("contem "+ this.disciplinaMapByCod.get(pre).getId() );
							
							continue;
						}
					}
				} catch (NullPointerException e) {
					continue;
				}
				//adiciona a disciplina a lista de disciplinas do periodo
				if (ForaDaOrdem) break;
				listaPeriodo.add(Integer.parseInt(L[i]));
				// quando conta 8 disciplinas coloca elas nas lista de periodos passados e zera a lista do periodos
				if (i%8==0) {
					listaPeriodosPassados.addAll(listaPeriodo);
					listaPeriodo.removeAll(listaPeriodo);
					//System.out.println("dispciplinas do passado alterada "+listaPeriodosPassados);
				}
			}
			
return ForaDaOrdem; 
		
		
	}

  
  
  
  
  
  
	/**
	 * metodo para testar se a solucao não
	 * tem choc de horário 
	 */

  
	public boolean verificaChoqueDeHorario(S Sugestao){		
		boolean chocouHorario =false;
		 boolean retorno =false;
		  List<S>copia= new ArrayList();
		  	copia.add(Sugestao);
				String[] L;
				L=copia.toString().split(" ");
				String tempo="";
				int w=0;
				for (String S: L){			
					if (S.equals("Objectives:")){
						tempo=L[w+1];
						break;
					}
					w+=1;
				}
			for (String S:L){
				float temp=Float.parseFloat(tempo);
				int fim = (int)temp;
				int modulo8=0;
				int proximoModulo8=8;
				String[] horario1;
				String [] horario2;
				
				for (int i=1;i<=(fim*8);i++){
					if (L[i]!="0"){		
						for (modulo8=i+1;modulo8<=proximoModulo8;modulo8++){
							try {
								horario1=this.disciplinaMap.get(L[i]).getDiaHora();
								horario2=this.disciplinaMap.get(L[modulo8]).getDiaHora();
								 for (int k=0;k<5;k++){
									 
										if ((horario1[k].equals(horario2[k]))&&(!horario1[k].equals(""))&&(!horario2[k].equals(""))){
											retorno =true;
											break;
										}								
									
								 }
							} catch (NullPointerException e) {
								continue;
							}
							if(retorno) break;
						}
					}
					if (i%8==0) proximoModulo8+=8;
					if(retorno) break;
				}
			}
		return retorno;
	}

  
	/**
	 * metodo para testar se a solucao contempla todas
	 * as obrigatorias 
	 */
  
  protected boolean verificaObrigatorias(S Sugestao){
	  boolean retorno =false;
	  List<S>copia= new ArrayList<>();
	  	copia.add(Sugestao);
			String[] L=copia.toString().split(" ");
			List<Disciplina>copiaObrigatorias= new ArrayList<>();
			copiaObrigatorias.addAll(this.DisciplinasObrigatorias);
		for (Disciplina D:this.DisciplinasObrigatorias){
			for (String S:L){
				
					if (S.equals("Objectives:")) break;
					if (S.equals(Integer.toString( D.getId())  )) {
						copiaObrigatorias.remove(D);
						break;
					}
			}
		}
		if (copiaObrigatorias.size()==0) retorno=true;
	  
	 return retorno; 
  }

	/**
	 * metodo para testar se a solucao tem periodos com menos de 3 
	 * disciplinas 
	 */

  protected boolean verificaMenorQueTres(S Sugestao){
	    String entrada = Sugestao.toString();
		ArrayList<Integer> qtd  = new ArrayList<Integer>();
		entrada = entrada.replace("Variables:", ""); //subtitui variables: por espaço vazio 
		entrada = entrada.trim(); //remove espaço vazio criado anteriormente
		String [] sugestao = entrada.split(" ");
		//System.out.println(sugestao.length);
		int fim = 0;
		for (int i=1; i<sugestao.length; i++){ //procura o fim da sequencia de cadeiras
			if (sugestao[i].equals("Objectives:")){
				fim = i-7;
				break;
			}
		}
		fim  = fim/8;
		int k = 0;
		int contar = 0;
		for (int i=0; i<=fim; i++){ // cada i corresponde a 1 semestre
			int livre = 0;
			for (int j=0; j<8; j++){ //verifica quantas cadeiras livres tem o semestre
				if (sugestao[j+k].equals("0")){
					livre ++;
				}
				//System.out.println(sugestao[j+k]);
				
			}
			qtd.add(livre);
			if (livre>5 && livre<8){ //verifica quantos semestres tem nais de tres cadeiras livres e disconsidera os semestres zerados
				contar++;
			
			}
			//System.out.println(contar);
			k = k+8;
			
		}
		boolean checar = false;
		for (int i=1; i<qtd.size()-1; i++){ //este metodo
			if (qtd.get(i)!=0 && qtd.get(i-1)==0){
				checar = true;
			}
		}
		
		if (contar!=0){
			checar = true;
		}
		return checar; 

	}

  
  
//  protected boolean verificaMenorQueTres(S Sugestao){
//	  boolean retorno =false;
//	  List<S>copia= new ArrayList();
//	  copia.add(Sugestao);
//			String[] L;
//			L=copia.toString().split(" ");
//			int w=0;
//			String tempo="";
//			//for para coletar a informação
//			//de tempo de fim de curso em 
//			// numero de periodos
//			for (String S: L){			
//				if (S.equals("Objectives:")){
//					tempo=L[w+1];
//					break;
//				}
//				w+=1;
//			}
//			float temp=Float.parseFloat(tempo);
//			int fim = (int)temp;
//			int contaCadeira=0;
//			int modulo8=0;
//			List <String> periodo = new ArrayList <>();
//			for (int i=1; i<(fim*8)+1;i++){
//				if (!L[i].equals("0")) periodo.add(L[i]);
//				if (i!=0&&i%8==0){
//					if (periodo.size()<3){ 
//						retorno=true;
//						break;
//						}
//					periodo.removeAll(periodo);
//				}
//			}
//			
//	  return retorno;
//  }
  
  /**
   * este metodo apersa do seu nome não verifica completamente se o aluno se forma
   * para isso outros metodos tambem tem de ser acionado
   * este metodo verifica se todas as disciplinas ofertadas estão dentro da lista
   *  de disciplinas não pagas e ele tambem verifica se o aluno atingi as 45
   *  disciplinas para se formar
   */
  public boolean seForma(S Sugestao){
	  boolean retorno =true;
	  List <Integer> naoPagasPeloId=new ArrayList<>();
	  List <Disciplina> discPagas=new ArrayList<>();
	  discPagas.addAll(this.preparacao.getAluno().getDiscPagas());
	  List<String>copia= new ArrayList<>();
	  String L[]=Sugestao.toString().split(" ");
	  
	  for (Disciplina D: this.preparacao.getNaoPagas()){
		  naoPagasPeloId.add(D.getId());
	  }
	  
	  for (int i=1;i<L.length;i++){ 
		if (L[i].equals("Objectives:")) break;
		
		  copia.add(L[i]);
	  }
	  
	  Disciplina D = new Disciplina(); 
	  int contadorDeDisc =0;
	  
	  for (String s:copia){
				if (!s.equals("0")){
					contadorDeDisc+=1;
					if (!naoPagasPeloId.contains(Integer.parseInt(s))) {
						retorno=false;
						break;
							}
					}
				}

		if ( (discPagas.size()+contadorDeDisc!=45)) retorno=false;	
	  
		return retorno;
  }
  
  
  /**
   * detecta sugestao com oferta da mesma disciplina mais de uma vez
   * 
   */
  public boolean disciplinasRepetidas(S Sugestao){
	  boolean retorno =false;
	  String L[]=Sugestao.toString().split(" ");
	  List<String>sugestaoLocal= new ArrayList<>();
	  for (String Z:L){
		  if (Z.equals("Objectives:")) break;
		  if (!Z.equals("0")){
			  if (sugestaoLocal.contains(Z)){
					retorno=true;// copia da população
							break;
						}else {
							sugestaoLocal.add(Z);
						}
					
				}
			}
	  return retorno;
  }
  
  /**
   * metodo que verifica se a disciplina esta no poeriodos correto
   * onde ela é ofertada: periodo par, periodo ímpar
   */
  
protected boolean VerificaSemestre(S Sugestao){
	  //Recupera o semestre de entrada e o atual
	  Date date= new Date();
	  Calendar cal = Calendar.getInstance();
	  cal.setTime(date);
	  int mes = cal.get(Calendar.MONTH) +1;
	  int Satual;
	  if (mes<=6){
		  Satual=1;
	  }else{
		  Satual=2;
	  }
	  	//trata a soluçao recebida
	  	String entrada = Sugestao.toString();
		entrada = entrada.replace("Variables:", ""); //subtitui variables: por espaço vazio 
		entrada = entrada.trim(); //remove espaço vazio criado anteriormente
		String [] sugestao = entrada.split(" ");
		int fim = 0;
		for (int i=1; i<sugestao.length; i++){ //procura o fim da sequencia de cadeiras
			if (sugestao[i].equals("Objectives:")){
				fim = i-7;
				break;
			}
		}
		//verifica a ocorrencia das cadeiras
		fim  = fim/8;
		int erro = 0;
		int k=0;
		int somatorio=0;
		boolean retorno = false;
		boolean pare=false;
		//outerloop:
		for (int i=0; i<=fim; i++){ // cada i corresponde a 1 semestre
			k = 8*i;
			for (int j=0; j<8; j++){
				if (!sugestao[j+k].equals("0")){
					somatorio=j+k;
					String id = (sugestao[somatorio]);
						if (this.disciplinaMap.get(id).getSemestre()==Satual || this.disciplinaMap.get(id).getSemestre()==0){ //Verifica se a cadeira esta no semestre certo
							retorno = false;
						}else{
							retorno = true;
							pare=true;
							break; //outerloop; //deve sair dos 2 loops
							
						}
					
					
			}
			
			
			}
			if (Satual==1){
				Satual=2;
			}else{
				Satual=1;
			}
			if(pare) break;
		}
		return retorno;
  }
  
  
  
  
 /**
  * metodo nativo do NSGAIII que foi modificado para atender as requisições de:
  * - nao considerar as solucoes com menos de 3 disciplinas por periodo
  * - nao considerar as solucoes sem todas as disciplinas obrigatorias (considerando
  * as que o aluno informou como ja paga)
  * - nao considerar as solucoes com choque de horário
  * - nao considerar as solucoes com sugestão fora de ordem de pre requisito
  * - nao considerar as solucoes com sugestoes onde o aluno não se forma, isso dependendo 
  *   se ele pagou a qtd de disciplinas necessarias e se a sugestão contem as disciplinas 
  *   que ele ainda não pagou.
  *  - nao considerar as solucoes com disciplinas repetidas.
  */
 protected void addRankedSolutionsToPopulation(Ranking<S> ranking, int rank, List<S> population) {
    List<S> front ;
    
    
    front = ranking.getSubfront(rank);
    for (int i = 0 ; i < front.size(); i++) {
    	
    	if (
			(!verificaMenorQueTres(front.get(i))) && 
			(verificaObrigatorias(front.get(i))) && 
			(!verifivaForaDeOrdemDePreRequisito(front.get(i)))&&
			(!verificaChoqueDeHorario(front.get(i)))&&
			(seForma(front.get(i)))&& 
			(!disciplinasRepetidas(front.get(i)))&&
			(!VerificaSemestre(front.get(i)))
			){

//    		System.out.println(" ");
//    		System.out.println(front.get(i));
//    		System.out.println("############################################################");
//    		System.out.println(verificaMenorQueTres(front.get(i)));
//    		System.out.println(verificaObrigatorias(front.get(i)));
//    		System.out.println(verificaChoqueDeHorario(front.get(i)));
//    		System.out.println(verifivaForaDeOrdemDePreRequisito(front.get(i)));
//    		System.out.println(seForma(front.get(i)));
//    		System.out.println("############################################################");
//    		System.out.println(" ");
    		population.add(front.get(i));
    		}
    }
  }

  protected List<S> getNonDominatedSolutions(List<S> solutionList) {
    return SolutionListUtils.getNondominatedSolutions(solutionList) ;
  }

  @Override public String getName() {
    return "NSGAIII" ;
  }

  @Override public String getDescription() {
    return "Nondominated Sorting Genetic Algorithm version III" ;
  }

}
