package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class LineTestFixture {
    public static ExtractableResponse<Response> 지하철_노선_등록(String name, String color){
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회(Long id){
        return RestAssured
                .given().log().all()
                .pathParam("id", id)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/{id}")
                .then().log().all()
                .extract();
    }
}
