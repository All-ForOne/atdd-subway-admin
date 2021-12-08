package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineTestFixture;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationTestFixture;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    private StationResponse stationUp;
    private StationResponse stationDown;
    private StationResponse stationAdd;
    private StationResponse stationAdd2;
    private LineResponse line;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        stationUp = StationTestFixture.지하철역_등록("강남역").as(StationResponse.class);
        stationDown = StationTestFixture.지하철역_등록("광교역").as(StationResponse.class);
        stationAdd = StationTestFixture.지하철역_등록("교대역").as(StationResponse.class);
        stationAdd2 = StationTestFixture.지하철역_등록("서울역").as(StationResponse.class);
        line = LineTestFixture.지하철_노선_등록("1호선", "blue", stationUp.getId(), stationDown.getId(), 10).as(LineResponse.class);
    }

    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void addSection() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = SectionTestFixture.지하철_노선에_지하철역_등록_요청(line.getId(), stationUp.getId(), stationAdd.getId(), 4);

        // then
        // 지하철_노선에_지하철역_등록됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(1L, 3L, 2L);
    }

    @DisplayName("노선에 등록하려는 구간이 이미 등록되어 있다.")
    @Test
    void addSectionIsSame() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = SectionTestFixture.지하철_노선에_지하철역_등록_요청(line.getId(), stationUp.getId(), stationDown.getId(), 5);

        // then
        // 지하철_노선에_지하철역_등록됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선에 존재하지 않는 역을 구간으로 등록한다.")
    @Test
    void addSectionNotExist() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = SectionTestFixture.지하철_노선에_지하철역_등록_요청(line.getId(), stationAdd.getId(), stationAdd2.getId(), 5);

        // then
        // 지하철_노선에_지하철역_등록됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존과 같은 길이의 구간을 등록한다.")
    @Test
    void addSectionBiggerDistance() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = SectionTestFixture.지하철_노선에_지하철역_등록_요청(line.getId(), stationUp.getId(), stationAdd.getId(), 10);

        // then
        // 지하철_노선에_지하철역_등록됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선 맨 앞에 구간을 등록한다.")
    @Test
    void addSectionFist() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = SectionTestFixture.지하철_노선에_지하철역_등록_요청(line.getId(), stationAdd.getId(), stationUp.getId(), 4);

        // then
        // 지하철_노선에_지하철역_등록됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(3L, 1L, 2L);
    }


    @DisplayName("노선 맨 뒤에 구간을 등록한다.")
    @Test
    void addSectionLast() {
        // when
        // 지하철_노선에_지하철역_등록_요청
        ExtractableResponse<Response> response = SectionTestFixture.지하철_노선에_지하철역_등록_요청(line.getId(), stationDown.getId(), stationAdd.getId(), 4);

        // then
        // 지하철_노선에_지하철역_등록됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(1L, 2L, 3L);
    }

    @DisplayName("역 ID로 첫번째 구간을 삭제한다.")
    @Test
    void deleteFirstSectionByStationId() {
        ExtractableResponse<Response> response = SectionTestFixture.지하철_노선에_지하철역_등록_요청(line.getId(), stationDown.getId(), stationAdd.getId(), 4);

        // when
        ExtractableResponse<Response> deleteResponse = SectionTestFixture.역_ID로_구간_삭제_요청(line.getId(), stationUp.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());


        ExtractableResponse<Response> lineResponse = LineTestFixture.지하철_노선_조회(line.getId());
        assertThat(lineResponse.jsonPath().getList("stations.id", Long.class)).containsExactly(2L, 3L);
    }

    @DisplayName("구간이 하나만 있는 경우 구간을 삭제하면 예외가 발생한다.")
    @Test
    void deleteLastSectionByStationId() {
        // when
        ExtractableResponse<Response> deleteResponse = SectionTestFixture.역_ID로_구간_삭제_요청(line.getId(), stationUp.getId());

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("노선에 존재하지 않는 역을 삭제하면 예외가 발생한다.")
    @Test
    void deleteNonExistSectionByStationId() {
        ExtractableResponse<Response> response = SectionTestFixture.지하철_노선에_지하철역_등록_요청(line.getId(), stationDown.getId(), stationAdd.getId(), 4);

        // when
        ExtractableResponse<Response> deleteResponse = SectionTestFixture.역_ID로_구간_삭제_요청(line.getId(), stationAdd2.getId());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("중간 구간을 삭제한다.")
    @Test
    void deleteMiddleSectionByStationId() {
        ExtractableResponse<Response> addResponse = SectionTestFixture.지하철_노선에_지하철역_등록_요청(line.getId(), stationDown.getId(), stationAdd.getId(), 4);
        ExtractableResponse<Response> add2Response = SectionTestFixture.지하철_노선에_지하철역_등록_요청(line.getId(), stationAdd.getId(), stationAdd2.getId(), 4);

        // when
        ExtractableResponse<Response> deleteResponse = SectionTestFixture.역_ID로_구간_삭제_요청(line.getId(), stationAdd.getId());

        // then
        assertThat(addResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(add2Response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        ExtractableResponse<Response> lineResponse = LineTestFixture.지하철_노선_조회(line.getId());
        assertThat(lineResponse.jsonPath().getList("stations.id", Long.class)).containsExactly(1L, 2L, 4L);
    }
}
