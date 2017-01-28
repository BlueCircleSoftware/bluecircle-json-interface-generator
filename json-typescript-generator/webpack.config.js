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

var failPlugin = require("webpack-fail-plugin");

module.exports = {
    entry: './src/test/typescript/header.spec.ts',
    output: {
        filename: 'target/bundle.js'
    }, // Enable sourcemaps for debugging webpack's output.
    devtool: "source-map",
    resolve: {
        extensions: ['.ts', '.js', '.tsx', '.jsx', '']
    },
    module: {
        loaders: [{
            test: /\.tsx?$/,
            loader: 'awesome-typescript-loader'
        }],
        preLoaders: [// All output '.js' files will have any sourcemaps re-processed by 'source-map-loader'.
            {
                test: /\.js$/,
                loader: "source-map-loader"
            }]

    },
    plugins: [failPlugin // fail on error (why not default?)
    ]
};