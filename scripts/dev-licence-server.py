#!/usr/bin/env python3
"""Small local AlphaSeries licence endpoint for development boot checks.

This serves a minimal licence response that exercises the Java validation path
without depending on the historical alpha-series.com endpoint.
"""

from http.server import BaseHTTPRequestHandler, HTTPServer
import argparse


DEFAULT_RESPONSE = "rank=4\r7:4=1"


class LicenceHandler(BaseHTTPRequestHandler):
    response_text = DEFAULT_RESPONSE

    def do_GET(self):
        body = self.response_text.encode("utf-8")
        self.send_response(200)
        self.send_header("Content-Type", "text/plain; charset=utf-8")
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)

    def log_message(self, fmt, *args):
        print("%s - %s" % (self.client_address[0], fmt % args))


def main():
    parser = argparse.ArgumentParser(description="Run a local AlphaSeries licence validation endpoint.")
    parser.add_argument("--host", default="127.0.0.1")
    parser.add_argument("--port", type=int, default=18080)
    parser.add_argument("--response", default=DEFAULT_RESPONSE)
    args = parser.parse_args()

    LicenceHandler.response_text = args.response
    server = HTTPServer((args.host, args.port), LicenceHandler)
    print("Serving AlphaSeries dev licence endpoint on http://%s:%s/check_product_sep11" % (args.host, args.port))
    server.serve_forever()


if __name__ == "__main__":
    main()
