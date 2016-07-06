#!/usr/bin/env bash

   for file in `ls out`
    do


        curl -s -XPOST localhost:9200/_bulk --data-binary "@out/${file}" > result.post.2.index.log

    done
