namespace java es.udc.ws.app.thrift

struct ThriftEventDto {
    1: i64 eventId
    2: string name
    3: string description
    4: i16 duration
    5: string celebrationDate
    6: bool cancelled
    7: i32 numSi
    8: i32 numNo
}

struct ThriftResponseDto {
    1: i64 responseId
    2: i64 eventId
    3: string userEmail
    4: bool attending
}

exception ThriftInputValidationException {
    1: string message
}

exception ThriftInstanceNotFoundException {
    1: string instanceId
    2: string instanceType
}

exception ThriftResponseDeadlineException {
    1: i64 eventId
}

exception ThriftEventAlreadyCelebratedException {
    1: i64 eventId
}

exception ThriftEventAlreadyCancelledException {
    1: i64 eventId
}

exception ThriftEventAlreadyRespondedException {
    1: i64 eventId,
    2: string email
}

exception ThriftDateTimeParseException {
    1: string message
}

service ThriftEventService {

   ThriftEventDto addEvent(1: ThriftEventDto eventDto) throws (1: ThriftInputValidationException e)

   list<ThriftEventDto> findByDate(1: string untilDate, 2: string keywords)
           throws (1: ThriftInputValidationException e, 2: ThriftDateTimeParseException ee)

   void cancelEvent(1: i64 eventId)
           throws (1: ThriftInstanceNotFoundException e1,
                   2: ThriftInputValidationException e2,
                   3: ThriftEventAlreadyCancelledException e3,
                   4: ThriftEventAlreadyCelebratedException e4)

   list<ThriftResponseDto> getResponses(1: string userEmail, 2: bool affirmatives)
}