package wei.tools.config;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import wei.tools.enums.ErrorCodeEnum;
import wei.tools.exception.ToolsException;

import java.io.IOException;
import java.net.URI;

@Component
public class ApiErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = HttpStatus.resolve(response.getRawStatusCode());
        return (statusCode != null && hasError(statusCode));
    }
    protected boolean hasError(HttpStatus statusCode) {
        return (statusCode.series() == HttpStatus.Series.CLIENT_ERROR ||
                statusCode.series() == HttpStatus.Series.SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        HttpStatus status = HttpStatus.resolve(response.getRawStatusCode());
        throw new ToolsException(status.value(), ErrorCodeEnum.CALL_WRONG.getMessage()+url+" ; error : " + status.name());
    }
}
