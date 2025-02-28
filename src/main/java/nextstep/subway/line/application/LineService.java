package nextstep.subway.line.application;

import nextstep.subway.exception.NotFoundException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LinesResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LineService {
    private LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse saveLine(LineRequest request) {
        Line persistLine = lineRepository.save(request.toLine());
        return LineResponse.of(persistLine);
    }

    public LinesResponse getLines() {
        List<Line> lines = lineRepository.findAll();
        return new LinesResponse(lines);
    }

    public LineResponse getLine(Long id) throws NotFoundException {
        Optional<Line> persistLine = lineRepository.findById(id);
        return LineResponse.of(persistLine.orElseThrow(NotFoundException::new));
    }

    public LineResponse updateLine(Long id, LineRequest request) throws NotFoundException {
        Line line = lineRepository.findById(id)
                                  .orElseThrow(NotFoundException::new);
        line.update(new Line(request.getName(), request.getColor()));
        return LineResponse.of(line);
    }

    public LineResponse deleteLine(Long id) {
        Line line = lineRepository.findById(id)
                                  .orElseThrow(NotFoundException::new);
        lineRepository.delete(line);
        return LineResponse.of(line);
    }
}
