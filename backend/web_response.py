class WebResponse:
    def __init__(self, data):
        self.httpStatusCode = 200
        self.additionalStatusCode = None
        self.message = None
        self.data = data