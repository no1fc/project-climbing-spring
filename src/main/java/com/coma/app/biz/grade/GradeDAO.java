package com.coma.app.biz.grade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class GradeDAO {
	//데이터 추가 GRADE_PROFILE, GRADE_NAME, GRADE_MIN_POINT, GRADE_MAX_POINT
	private final String INSERT = "INSERT INTO GRADE(GRADE_PROFILE, GRADE_NAME, GRADE_MIN_POINT, GRADE_MAX_POINT) VALUES(?, ?, ?, ?)";
	//max_point기준으로 내림차순 정렬해서 출력
	private final String ALL_DESC = "SELECT GRADE_NUM, GRADE_PROFILE, GRADE_NAME, GRADE_MIN_POINT, GRADE_MAX_POINT FROM GRADE ORDER BY GRADE_MAX_POINT DESC";
	//PK로 등급 찾기 GRADE_NUM
	private final String ONE = "SELECT GRADE_NUM, GRADE_PROFILE, GRADE_NAME, GRADE_MIN_POINT, GRADE_MAX_POINT FROM GRADE WHERE GRADE_NUM = ?";


	@Autowired
	private JdbcTemplate jdbcTemplate; // 스프링부트 내장객체

	public boolean insert(GradeDTO gradeDTO) {
		//데이터 추가 GRADE_PROFILE, GRADE_NAME, GRADE_MIN_POINT, GRADE_MAX_POINT
		int result=jdbcTemplate.update(INSERT,gradeDTO.getGrade_profile(),gradeDTO.getGrade_name(),gradeDTO.getGrade_min_point(),gradeDTO.getGrade_max_point());
		if(result<=0) {
			return false;
		}
		return true;
	}

	public boolean update(GradeDTO gradeDTO) { // TODO 없는 CRUD
		return false;
	}

	public boolean delete(GradeDTO gradeDTO) { // TODO 없는 CRUD
		return false;
	}

	public GradeDTO selectOne(GradeDTO gradeDTO){
		GradeDTO data= null;
		Object[] args = {gradeDTO.getGrade_num()};
		try {
			//PK로 등급 찾기 GRADE_NUM
			data= jdbcTemplate.queryForObject(ONE,args,new GradeRowMapper());
		}
		catch(Exception e) {
		}
		return data;
	}

	public List<GradeDTO> selectAll(GradeDTO gradeDTO){
		List<GradeDTO> datas = null;
		try {
			//max_point기준으로 내림차순 정렬해서 출력
			datas = jdbcTemplate.query(ALL_DESC, new GradeRowMapper());
		}
		catch (Exception e) {
		}
		return datas;
	}
}

class GradeRowMapper implements RowMapper<GradeDTO> {

	public GradeDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
		GradeDTO gradeDTO=new GradeDTO();
		System.out.print("FavoriteRowMapper DB에서 가져온 데이터 {");
		gradeDTO.setGrade_num(rs.getInt("GRADE_NUM"));
		System.err.println("gym_num = ["+gradeDTO.getGrade_num()+"]");
		gradeDTO.setGrade_profile(rs.getString("GRADE_PROFILE"));
		System.err.println("gym_profile = ["+gradeDTO.getGrade_profile()+"]");
		gradeDTO.setGrade_name(rs.getString("GRADE_NAME"));
		System.err.println("gym_name = ["+gradeDTO.getGrade_name()+"]");
		gradeDTO.setGrade_min_point(rs.getInt("GRADE_MIN_POINT"));
		System.err.println("gym_min_point = ["+gradeDTO.getGrade_min_point()+"]");
		gradeDTO.setGrade_max_point(rs.getInt("GRADE_MAX_POINT"));
		System.err.println("gym_max_point = ["+gradeDTO.getGrade_max_point()+"]");
		System.out.println("}");
		return gradeDTO;
	}
}
