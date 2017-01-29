package sisaJmetalDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import sisaJmetalbeans.Area;
import sisaJmetalbeans.Conexao;
import sisaJmetalbeans.ConexaoPostgresql;
import sisaJmetalbeans.Disciplina;

/**
 * @author LENOVO
 *
 */
public class DisciplinaDAO {
	
/**
 * Este m�todo � respons�vel por recuperar todos os dados no DB e armazenar em um List.
 *
 */		
	public List<Disciplina> getDisciplinas(){
		
		Connection con = ConexaoPostgresql.getConnection();
		
		//Conex�o para o Mysql - Jorge
		//Connection con = Conexao.getConnection();
		
		try {
			
			List<Disciplina> disciplinas = new ArrayList<>();
			String sql = "SELECT * FROM disciplinas";
			PreparedStatement pstmt = con.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			//String[] diaHora=new String[5];
			while (rs.next()) {
//				/**
//				 *para msql 
//				 */
//				Disciplina disciplinaTemporaria = new Disciplina();
//				Area areaTemporaria = new Area();
//				disciplinaTemporaria.setPeriodo(rs.getString("periodo"));
//				disciplinaTemporaria.setId(rs.getInt("id"));
//				disciplinaTemporaria.setCodigo(rs.getInt("c�digo"));
//				disciplinaTemporaria.setNome(rs.getString("nome"));
//				areaTemporaria.setNome(rs.getString("�rea"));
//				disciplinaTemporaria.setArea(areaTemporaria);
//				disciplinaTemporaria.setPreRequisitos(rs.getString("Pr�-requisitos"));
//				disciplinaTemporaria.setSemestre(rs.getInt("semestre"));
//				disciplinaTemporaria.setM�diageral(rs.getString("m�dia geral"));
//				disciplinaTemporaria.setGrauDificuldade(rs.getString("dificudade"));
//				diaHora[0]=rs.getString("segunda");
//				diaHora[1]=rs.getString("ter�a");
//				diaHora[2]=rs.getString("quarta");
//				diaHora[3]=rs.getString("quinta");
//				diaHora[4]=rs.getString("sexta");
//				disciplinaTemporaria.setDiaHora(diaHora);
//				disciplinas.add(disciplinaTemporaria);
				
				/**
				 * para postgrees
				 */
				try {
					
				} catch (Exception e) {
					// TODO: handle exception
				}
				Disciplina disciplinaTemporaria = new Disciplina();
				String[] diaHora=new String[5];
				Area areaTemporaria = new Area();
				disciplinaTemporaria.setPeriodo(rs.getString("periodo"));
				disciplinaTemporaria.setId(rs.getInt("id"));
				disciplinaTemporaria.setCodigo(rs.getInt("c�digo"));
				disciplinaTemporaria.setNome(rs.getString("nome"));
				areaTemporaria.setNome(rs.getString("�rea"));
				disciplinaTemporaria.setArea(areaTemporaria);
				disciplinaTemporaria.setPreRequisitos(rs.getString("pr�requisitos"));
				disciplinaTemporaria.setSemestre(rs.getInt("semestre"));
				disciplinaTemporaria.setMediageral(rs.getString("m�diageral"));
				disciplinaTemporaria.setGrauDificuldade(rs.getString("dificudade"));
				diaHora[0]=rs.getString("segunda");
				diaHora[1]=rs.getString("ter�a");
				diaHora[2]=rs.getString("quarta");
				diaHora[3]=rs.getString("quinta");
				diaHora[4]=rs.getString("sexta");
//				System.out.println( rs.getString("nome")+"[segundas "+diaHora[0]+"ter�as "+diaHora[1]+"quartas"
//				+diaHora[2]+"quintas "+diaHora[3]+"sextas"+diaHora[4]+"]");
				disciplinaTemporaria.setDiaHora(diaHora);
//				System.out.println( disciplinaTemporaria.getNome()+"[segundas "+disciplinaTemporaria.getDiaHora()[0]+
//						"ter�as "+disciplinaTemporaria.getDiaHora()[1]+"quartas"
//						+disciplinaTemporaria.getDiaHora()[2]+"quintas "
//						+disciplinaTemporaria.getDiaHora()[3]+
//						"sextas"+disciplinaTemporaria.getDiaHora()[4]+"]");				
				disciplinas.add(disciplinaTemporaria);

				
				
			}
			rs.close();
			pstmt.close();
//			for (Disciplina D:disciplinas){
//				
//				System.out.println( D.getNome()+"[segundas "+D.getDiaHora()[0]+"ter�as "+D.getDiaHora()[1]+"quartas"
//				+D.getDiaHora()[2]+"quintas "+D.getDiaHora()[3]+"sextas"+D.getDiaHora()[4]+"]");
//		
//				System.out.println("["+D.getDiaHora()[0]+" "+ ""+" "+D.getDiaHora()[1]+" "+ ""+D.getDiaHora()[2]+""
//					+ " "+ ""+D.getDiaHora()[3]+" "+ ""+D.getDiaHora()[4]+"]");
//			}
			return disciplinas;
		} catch (SQLException e) {
			// TODO: handle exception
			throw new RuntimeException(e);
		}
				
	}
	
}
