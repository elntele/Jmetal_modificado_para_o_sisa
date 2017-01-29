package org.uma.jmetal.algorithm.multiobjective.nsgaiii;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmBuilder;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import sisaJMetalMain.PreparacaoDoProblema;
import sisaJmetalbeans.Disciplina;

import java.util.ArrayList;
import java.util.List;


/** Builder class */
public class NSGAIIIBuilder<S extends Solution<?>> implements AlgorithmBuilder<NSGAIII<S>>{
  
  // no access modifier means access from classes within the same package
  private Problem<S> problem ;
  private int maxIterations ;
  private int populationSize ;
  private CrossoverOperator<S> crossoverOperator ;
  private MutationOperator<S> mutationOperator ;
  private SelectionOperator<List<S>, S> selectionOperator ;

  private SolutionListEvaluator<S> evaluator ;
  /**
   * incluido por jorge candeias para os fins do projeto
   * sisa
   */
  private PreparacaoDoProblema preparacao;

/** Builder constructor */
  public NSGAIIIBuilder(Problem<S> problem,PreparacaoDoProblema preparacao) {
    this.problem = problem ;
    this.preparacao=preparacao;
    maxIterations = 250 ;
    populationSize = 100 ;
    evaluator = new SequentialSolutionListEvaluator<S>() ;
  }

  public NSGAIIIBuilder<S> setMaxIterations(int maxIterations) {
    this.maxIterations = maxIterations ;

    return this ;
  }

  public NSGAIIIBuilder<S> setPopulationSize(int populationSize) {
    this.populationSize = populationSize ;

    return this ;
  }

  public NSGAIIIBuilder<S> setCrossoverOperator(CrossoverOperator<S> crossoverOperator) {
    this.crossoverOperator = crossoverOperator ;

    return this ;
  }

  public NSGAIIIBuilder<S> setMutationOperator(MutationOperator<S> mutationOperator) {
    this.mutationOperator = mutationOperator ;

    return this ;
  }

  public NSGAIIIBuilder<S> setSelectionOperator(SelectionOperator<List<S>, S> selectionOperator) {
    this.selectionOperator = selectionOperator ;

    return this ;
  }

  public NSGAIIIBuilder<S> setSolutionListEvaluator(SolutionListEvaluator<S> evaluator) {
    this.evaluator = evaluator ;

    return this ;
  }

  public SolutionListEvaluator<S> getEvaluator() {
    return evaluator;
  }

  public Problem<S> getProblem() {
    return problem;
  }

  public int getMaxIterations() {
    return maxIterations;
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public CrossoverOperator<S> getCrossoverOperator() {
    return crossoverOperator;
  }

  public MutationOperator<S> getMutationOperator() {
    return mutationOperator;
  }

  public SelectionOperator<List<S>, S> getSelectionOperator() {
    return selectionOperator;
  }
  
  
  /**
   * incluido por jorge candeias para os fins do projeto
   * sisa
   */

  public PreparacaoDoProblema getPreparacao() {
	return preparacao;
}

public NSGAIII<S> build() {
    return new NSGAIII<>(this) ;
  }
}
