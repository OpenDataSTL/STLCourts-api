package svc.data.citations.datasources.mock;

import org.springframework.stereotype.Repository;

import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.dao.DataAccessException;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

import svc.data.jdbc.BaseJdbcDao;
import svc.logging.LogSystem;
import svc.models.*;

@Repository
public class ViolationDAO extends BaseJdbcDao
{	
	public List<Violation> getViolationsByCitationNumber(String citationNumber)
	{
		List<Violation> violations;
		try{
			Map<String, Object> parameterMap = new HashMap<String, Object>();
	        parameterMap.put("citationNumber", citationNumber);
			String sql = "SELECT * FROM violations WHERE citation_number = :citationNumber";
			violations = jdbcTemplate.query(sql, parameterMap,new ViolationSQLMapper());
		}catch (Exception e) {
            LogSystem.LogDBException(e);
            violations = new ArrayList<Violation>();
        }
		return violations;
	}
	
	public boolean insertViolations(List<Violation> violations){
		try{
			for(int i = 0; i < violations.size(); i++){
				Violation v = violations.get(i);
				String sql = "INSERT INTO violations (citation_number,violation_number,violation_description,warrant_status,warrant_number,status,fine_amount,court_cost) VALUES ('"+v.citation_number+"','"+v.violation_number+"','"+v.violation_description+"',"+v.warrant_status+",'"+v.warrant_number+"','"+v.status.name()+"',"+v.fine_amount.toString()+","+v.court_cost.toString()+")";
				jdbcTemplate.execute(sql, new PreparedStatementCallback<Boolean>(){
					@Override
					public Boolean doInPreparedStatement(java.sql.PreparedStatement ps)
							throws SQLException, DataAccessException {
						return ps.execute();
					}
				});
			}	
		}catch(Exception e){
			LogSystem.LogDBException(e);
			return false;
		}
		return true;
	}
	
	public boolean removeViolations(List<Violation> violations){
		try{
			for(int i = 0; i < violations.size(); i++){
				Violation v = violations.get(i);
				String sql = "DELETE FROM violations WHERE citation_number = '"+v.citation_number+"'";
				jdbcTemplate.execute(sql, new PreparedStatementCallback<Boolean>(){
					@Override
					public Boolean doInPreparedStatement(java.sql.PreparedStatement ps)
							throws SQLException, DataAccessException {
						return ps.execute();
					}
				});
			}
		}catch(Exception e){
			LogSystem.LogDBException(e);
			return false;
		}
		return true;
	}
	
		 
	private class ViolationSQLMapper implements RowMapper<Violation>
	{
		public Violation mapRow(ResultSet rs, int i) throws SQLException
		{
			Violation violation = new Violation();
			try
			{	
				violation.id = rs.getInt("id");
				violation.citation_number = rs.getString("citation_number");
				violation.violation_number = rs.getString("violation_number");
				violation.violation_description = rs.getString("violation_description");
				violation.warrant_status = rs.getBoolean("warrant_status");
				violation.warrant_number = rs.getString("warrant_number");
				violation.status = VIOLATION_STATUS.convertDatabaseStatusToEnum(rs.getString("status"));
				violation.can_pay_online = rs.getBoolean("can_pay_online");
				String fineAmountStr = rs.getString("fine_amount");
				if (fineAmountStr != null)
				{
					violation.fine_amount = new BigDecimal(fineAmountStr.replace('$', ' ').trim());
				}
				String courtCostStr = rs.getString("court_cost");
				if (courtCostStr != null)
				{
					violation.court_cost = new BigDecimal(courtCostStr.replace('$', ' ').trim());
				}
			}
			catch (NullPointerException e)
			{
				LogSystem.LogEvent("Null Pointer while processing DB rows - " + e.getMessage() + ":" + e.getCause().toString());
				return null;
			}
			catch (Exception e)
			{
				LogSystem.LogDBException(e);
				return null;
			}
			
			return violation;
		}
	}
}
