import http from "k6/http";
import { check } from "k6";

export default function() {
  let data = JSON.stringify({"username": "gbarnett", "text": "abba"});
  let res = http.post("http://localhost:8080/api/v1/palindrome", data, {
    headers: {"Content-Type": "application/json"}
  });
  check(res, {
    "status is 200": (r) => r.status === 200
  });
}
