package com.example.demo.src.main.DAO;

import com.example.demo.src.main.model.GetEventDetailRes;
import com.example.demo.src.main.model.GetEventsRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class MainDao {
    @Autowired
    private JdbcTemplate        jdbcTemplate;

    public void setDataSource(DataSource    dataSource){this.jdbcTemplate = new JdbcTemplate(dataSource);}

    public List<GetEventsRes>   retrieveEvents() {
        String      retrieveEventsQuery = "SELECT\n" +
                "    eventId,\n" +
                "    bannerPic,\n" +
                "    CASE WHEN TIMESTAMPDIFF(SECOND, CURRENT_TIMESTAMP, due) < 0\n" +
                "         THEN '종료'\n" +
                "         WHEN TIMESTAMPDIFF(DAY, CURRENT_TIMESTAMP, due) < 1\n" +
                "         THEN '오늘 종료'\n" +
                "         ELSE concat(TIMESTAMPDIFF(DAY, CURRENT_TIMESTAMP, due),'일 남음') END\n" +
                "    AS 'due'\n" +
                "FROM\n" +
                "    rising_test.Events\n" +
                "WHERE\n" +
                "    TIMESTAMPDIFF(DAY, CURRENT_TIMESTAMP, due) > -30 AND\n" +
                "    TIMESTAMPDIFF(DAY, CURRENT_TIMESTAMP, due) < 30\n" +
                ";";

        return this.jdbcTemplate.query(retrieveEventsQuery,
                (rs, rowNum) -> new GetEventsRes(
                        rs.getLong("eventId"),
                        rs.getString("bannerPic"),
                        rs.getString("due")
                )
                );
    }

    public int  checkEventId(long   eventId){
        String      checkEventIdQuery = "SELECT EXISTS(\n" +
                "    SELECT eventId FROM rising_test.Events\n" +
                "    WHERE TIMESTAMPDIFF(DAY, CURRENT_TIMESTAMP, due) > -30 AND\n" +
                "          TIMESTAMPDIFF(DAY, CURRENT_TIMESTAMP, due)<30 AND\n" +
                "          eventId = ?\n" +
                "           );";
        long        checkEventIdQueryParams = eventId;

        return this.jdbcTemplate.queryForObject(checkEventIdQuery, int.class, checkEventIdQueryParams);
    }

    public GetEventDetailRes    retrieveEventDetails(long   eventId){
        String      retrieveEventDetailsQuery = "SELECT\n" +
                "    imgUrl\n" +
                "FROM    EventImgs   WHERE eventId = ?;";
        long        retrieveEventDetailsQueryParams = eventId;

        return  new GetEventDetailRes(eventId,
                this.jdbcTemplate.query(retrieveEventDetailsQuery,
                        (rs, rowNum)-> rs.getString("imgUrl")
                        ,retrieveEventDetailsQueryParams));
    }
}