FROM golang:1.23-alpine AS build
WORKDIR /build
COPY go.mod ./
RUN go mod download
COPY . .
RUN CGO_ENABLED=0 GOOS=linux go build -ldflags="-s -w" -o ufp-stream-processor .

FROM scratch
COPY --from=build /build/ufp-stream-processor /ufp-stream-processor
ENTRYPOINT ["/ufp-stream-processor"]
