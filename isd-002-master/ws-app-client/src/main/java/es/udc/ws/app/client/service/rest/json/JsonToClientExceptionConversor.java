package es.udc.ws.app.client.service.rest.json;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import es.udc.ws.app.client.service.exceptions.ClientEventAlreadyCancelledException;
import es.udc.ws.app.client.service.exceptions.ClientEventAlreadyCelebratedException;
import es.udc.ws.app.client.service.exceptions.ClientEventAlreadyRespondedException;
import es.udc.ws.app.client.service.exceptions.ClientResponseDeadlineException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;
import java.time.LocalDateTime;


public class JsonToClientExceptionConversor {
    public static Exception fromBadRequestErrorCode(InputStream ex) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(ex);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                String errorType = rootNode.get("errorType").textValue();
                if (errorType.equals("InputValidation")) {
                    return toInputValidationException(rootNode);
                } else {
                    throw new ParsingException("Unrecognized error type: " + errorType);
                }
            }
        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static InputValidationException toInputValidationException(JsonNode rootNode) {
        String message = rootNode.get("message").textValue();
        return new InputValidationException(message);
    }

    public static Exception fromNotFoundErrorCode(InputStream ex) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(ex);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                String errorType = rootNode.get("errorType").textValue();
                if (errorType.equals("InstanceNotFound")) {
                    return toInstanceNotFoundException(rootNode);
                } else {
                    throw new ParsingException("Unrecognized error type: " + errorType);
                }
            }
        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static InstanceNotFoundException toInstanceNotFoundException(JsonNode rootNode) {
        String instanceId = rootNode.get("instanceId").textValue();
        String instanceType = rootNode.get("instanceType").textValue();
        return new InstanceNotFoundException(instanceId, instanceType);
    }

    public static Exception fromForbiddenErrorCode(InputStream ex) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(ex);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                String errorType = rootNode.get("errorType").textValue();
                return switch (errorType) {
                    case "EventAlreadyCancelled" -> toEventAlreadyCancelledException(rootNode);
                    case "EventAlreadyCelebrated" -> toEventAlreadyCelebratedException(rootNode);
                    case "EventAlreadyResponded" -> toEventAlreadyRespondedException(rootNode);
                    default -> throw new ParsingException("Unrecognized error type: " + errorType);
                };
            }
        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static ClientEventAlreadyCancelledException toEventAlreadyCancelledException(JsonNode rootNode) {
        Long eventId = rootNode.get("eventId").longValue();
        return new ClientEventAlreadyCancelledException(eventId);
    }

    private static ClientEventAlreadyCelebratedException toEventAlreadyCelebratedException(JsonNode rootNode) {
        Long eventId = rootNode.get("eventId").longValue();
        return new ClientEventAlreadyCelebratedException(eventId);
    }

    private static ClientResponseDeadlineException toResponseDeadlineException(JsonNode rootNode) {
        Long eventId = rootNode.get("eventId").longValue();
        return new ClientResponseDeadlineException(eventId);
    }

    private static ClientEventAlreadyRespondedException toEventAlreadyRespondedException(JsonNode rootNode) {
        Long eventId = rootNode.get("eventId").longValue();
        String email = rootNode.get("userEmail").textValue().trim();

        return new ClientEventAlreadyRespondedException(eventId, email);
    }

    public static Exception fromGoneErrorCode(InputStream ex) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(ex);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                String errorType = rootNode.get("errorType").textValue();
                //if (errorType.equals("SaleExpiration")) {
                //  return toResponseExpirationException(rootNode);
                //} else {
                throw new ParsingException("Unrecognized error type: " + errorType);
                //}
            }
        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }
    /*
    private static ClientResponseExpirationException toResponseExpirationException(JsonNode rootNode) {
        Long responseId = rootNode.get("responseId").longValue();
        String expirationDateAsString = rootNode.get("expirationDate").textValue();
        LocalDateTime expirationDate = null;
        if (expirationDateAsString != null) {
            expirationDate = LocalDateTime.parse(expirationDateAsString);
        }
        return new ClientResponseExpirationException(responseId, expirationDate);
    }*/
}

