package org.mysqltutorial;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.sql.CallableStatement;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Main {
	public static void writeBlob(int candidateId, String filename) {
        // ����sql
        String updateSQL = "UPDATE candidates "
                + "SET resume = ? "
                + "WHERE id=?";
 
        try (Connection conn = MySQLJDBCUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
             // �Ķ��ļ�
            File file = new File(filename);
            FileInputStream input = new FileInputStream(file);
            // ���ò���
            pstmt.setBinaryStream(1, input);
            pstmt.setInt(2, candidateId);
            // �����ݿ��ﴢ��resume�ļ�
            System.out.println("Reading file " + file.getAbsolutePath());
            System.out.println("Store file in the database.");
            pstmt.executeUpdate();
 
        } catch (SQLException | FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
	public static void readBlob(int candidateId, String filename) {
        // ���� sql
        String selectSQL = "SELECT resume FROM candidates WHERE id=?";
        ResultSet rs = null;
 
        try (Connection conn = MySQLJDBCUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(selectSQL);) {
            // �������
            pstmt.setInt(1, candidateId);
            rs = pstmt.executeQuery();
          // ����������д���ļ�
            File file = new File(filename);
            FileOutputStream output = new FileOutputStream(file);
            System.out.println("Writing to file " + file.getAbsolutePath());
            while (rs.next()) {
                InputStream input = rs.getBinaryStream("resume");
                byte[] buffer = new byte[1024];
                while (input.read(buffer) > 0) {
                    output.write(buffer);
                }
            }
        } 
           catch (SQLException | IOException e) {
                  System.out.println(e.getMessage());
                    } 
          finally {
                       try {
                               if (rs != null) {
                                             rs.close();
                                         }
                          } 
                       catch (SQLException e) {
                          System.out.println(e.getMessage());
                  }
                }
             }

	public void update() {
		 
        String sqlUpdate = "UPDATE candidates "
                + "SET last_name = ? "
                + "WHERE id = ?";
 
        try (Connection conn = MySQLJDBCUtil.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {
 
            // ׼�����ݽ�������
            String lastName = "William";
            int id = 100;
            pstmt.setString(1, lastName);
            pstmt.setInt(2, id);
 
            int rowAffected = pstmt.executeUpdate();
            System.out.println(String.format("Row affected %d", rowAffected));
 
            // ����׼�����
            lastName = "Grohe";
            id = 101;
            pstmt.setString(1, lastName);
            pstmt.setInt(2, id);
 
            rowAffected = pstmt.executeUpdate();
            System.out.println(String.format("Row affected %d", rowAffected));
 
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
 
    /**
     * main method
     *
     * @param args
     */
	public static int insertCandidate(String firstName,String lastName,Date dob, 
            String email, String phone) {
             // �����µĺ�ѡ�� 
             ResultSet rs = null;
             int candidateId = 0;

             String sql = "INSERT INTO candidates(first_name,last_name,dob,phone,email) "
             + "VALUES(?,?,?,?,?)";

           try (Connection conn = MySQLJDBCUtil.getConnection();
           PreparedStatement pstmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);) {
 
       // ���������� 
         pstmt.setString(1, firstName);
         pstmt.setString(2, lastName);
         pstmt.setDate(3, dob);
         pstmt.setString(4, phone);
         pstmt.setString(5, email);

     int rowAffected = pstmt.executeUpdate();
      if(rowAffected == 1)
       {
           // �õ���ѡ�˵� id
         rs = pstmt.getGeneratedKeys();
         if(rs.next())
          candidateId = rs.getInt(1);

      }
  } 
           catch (SQLException ex) {
      System.out.println(ex.getMessage());
             } 
           finally {
                try {
                      if(rs != null)  rs.close();
                  } 
                catch (SQLException e) {
                   System.out.println(e.getMessage());
              }
         }

       return candidateId;
      }

	public static void addCandidate(String firstName,String lastName,Date dob, 
            String email, String phone, int[] skills) {

                 Connection conn = null;

                // Ϊ�˲���һ���µĺ�ѡ��
              PreparedStatement pstmt = null;

               // ���似��
             PreparedStatement pstmtAssignment = null;

             // Ϊ�˵õ���ѡ�� id
             ResultSet rs = null;

              try {
                   conn = MySQLJDBCUtil.getConnection();
               // �����Զ��ύ��false 
                  conn.setAutoCommit(false);

              // �����ѡ��

               String sqlInsert = "INSERT INTO candidates(first_name,last_name,dob,phone,email) "
                     + "VALUES(?,?,?,?,?)";

               pstmt = conn.prepareStatement(sqlInsert,Statement.RETURN_GENERATED_KEYS);

               pstmt.setString(1, firstName);
               pstmt.setString(2, lastName);
               pstmt.setDate(3, dob);
               pstmt.setString(4, phone);
               pstmt.setString(5, email);

            int rowAffected = pstmt.executeUpdate();

             // �õ���ѡ�� id
               rs = pstmt.getGeneratedKeys();
               int candidateId = 0;
               if(rs.next())
               candidateId = rs.getInt(1);   
              //�ڲ�������ɹ�������£����似�ܸ���ѡ��

                 if(rowAffected == 1)
                  {
                     //���似�� 
                    String sqlPivot = "INSERT INTO candidate_skills(candidate_id,skill_id) "
                        + "VALUES(?,?)";

                   pstmtAssignment = conn.prepareStatement(sqlPivot);
                      for(int skillId : skills) {

                         pstmtAssignment.setInt(1, candidateId);
                         pstmtAssignment.setInt(2, skillId);
                         pstmtAssignment.executeUpdate();
                         }
                       conn.commit();
                       } else 
                       {
                           conn.rollback();
                          }
                     } 
              catch (SQLException ex) {
                 // ���׻ع�
                  try{
                      if(conn != null)
                       conn.rollback();
                    }
                  catch(SQLException e){
                  System.out.println(e.getMessage());
                     }

                   System.out.println(ex.getMessage());
                       } 
              finally {
                       try {
                            if(rs != null)  rs.close();
                            if(pstmt != null) pstmt.close();
                            if(pstmtAssignment != null) pstmtAssignment.close();
                            if(conn != null) conn.close();

                           } 
                       catch (SQLException e) {
                                  System.out.println(e.getMessage());
                                 }
                          }
                        }

	   public static void getSkills(int candidateId) {
        
            String query = "{ call get_candidate_skill(?) }";
            ResultSet rs;
 
            try (Connection conn = MySQLJDBCUtil.getConnection();
                   CallableStatement stmt = conn.prepareCall(query)) {
 
                      stmt.setInt(1, candidateId);
                      rs = stmt.executeQuery();
                  while (rs.next()) {
                            System.out.println(String.format("%s - %s",
                                        rs.getString("first_name") + " "
                                        + rs.getString("last_name"),
                                        rs.getString("skill")));
                                    }
                       }
            catch (SQLException ex) {
                             System.out.println(ex.getMessage());
                         }
                      }

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try (Connection conn = MySQLJDBCUtil.getConnection())
				{
            
            // �����Ϣ
            System.out.println(String.format("Connected to database %s "
                    + "successfully.", conn.getCatalog()));
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

		String sql = "SELECT first_name, last_name, email " +
                "FROM candidates";
   
        try (Connection conn = MySQLJDBCUtil.getConnection();
        Statement stmt  = conn.createStatement();
        ResultSet rs    = stmt.executeQuery(sql)) {
      
       // ͨ�������ѭ��
       while (rs.next()) {
           System.out.println(rs.getString("first_name") + "\t" + 
                              rs.getString("last_name")  + "\t" +
                              rs.getString("email"));
               
       }
   } catch (SQLException ex) {
       System.out.println(ex.getMessage());
   }

	Main mysqlupdate = new Main();
    mysqlupdate.update();
    //����һ���µĺ�ѡ��
    int id = insertCandidate("Bush", "Lily", Date.valueOf("1980-01-04"), 
            "bush.l@yahoo.com", "(408) 898-6666");

    System.out.println(String.format("A new candidate with id %d has been inserted.",id));
    // ���벢�ҷ��似�� 
    int[] skills = {1,2,3};
    addCandidate("John", "Doe", Date.valueOf("1990-01-04"), 
                    "john.d@yahoo.com", "(408) 898-5641", skills);
    getSkills(122);
    writeBlob(122, "johndoe_resume.pdf");
    readBlob(122, "johndoe_resume_from_db.pdf"); 
	}

}
