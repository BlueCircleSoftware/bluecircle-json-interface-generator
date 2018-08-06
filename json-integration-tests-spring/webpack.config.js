/*
 * Copyright 2017 Blue Circle Software, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

var path = require("path");

module.exports = {
    entry: "./src/test/typescript/springTypeScriptIntegration.spec.ts",
    output: {
        filename: "bundle.js",
        path: path.resolve(__dirname, "target")
    }, // Enable sourcemaps for debugging webpack's output.
    devtool: "inline-source-map",
    mode: "development",
    resolve: {
        extensions: [".tsx", ".ts", ".jsx", ".js"]
    },
    module: {
        rules: [{
            test: /\.tsx?$/,
            loader: "awesome-typescript-loader"
        }, {
            test: /\.js$/,
            loader: "source-map-loader"
        }]

    },
};