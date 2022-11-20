import http from "k6/http";
import { check } from "k6";
import { randomString } from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';


export default function() {
  let data = JSON.stringify({"username": "gbarnett", "text": randomString(150)});
  let res = http.post("http://localhost:8080/api/v1/palindrome", data, {
    headers: {"Content-Type": "application/json"}
  });
  check(res, {
    "status is 200": (r) => r.status === 200
  });
}
