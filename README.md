# YourStoryOnZhihu
Zhihu daily app on Android not offical, only use for personal hobby


## API description

------------------

##### 1. 获取最新消息

`https://news-at.zhihu.com/api/4/stories/latest`

```json
{
    "date": "20170614",
    "stories": [
        {
            "ga_prefix": "061409",
            "id": 9473066,
            "images": [
                "https://pic2.zhimg.com/v2-6722e278219e08ba75e929bedc3a3ac5.jpg"
            ],
            "title": "她：我没生气\r\n求问 AI：她现在生没生气？",
            "type": 0
        }
    ],
    "top_stories": [
        {
            "ga_prefix": "061407",
            "id": 9473585,
            "image": "https://pic4.zhimg.com/v2-551486bad1002bee255a2403b19cac6b.jpg",
            "title": "且不说解说的功力，怎么连看台上的人詹俊都能认出来？",
            "type": 0
        }
    ]
}
```

##### 2. 获取特定日期的消息

* URL
`https://news-at.zhihu.com/api/4/stories/before/20170613`

* 返回值

`type`值不明, `ga_prefix`应该是日期+序列号条目

```json
{
  "date": "20170612",
  "stories": [
    {
      "images": [
        "https://pic2.zhimg.com/v2-09b3e633583a3421ce5c4d27413f5439.jpg"
      ],
      "type": 0,
      "id": 9472212,
      "ga_prefix": "061222",
      "title": "小事 · 我是如何考上清华的"
    }
  ]
}

```

##### 3. 单条消息详细

* URL 
`https://news-at.zhihu.com/api/4/story/9472025`
story后面是消息的`id`值

* 返回值

```json
{
  "body": "正文html",
  "image_source": "《梦想启动》",
  "title": "作为知乎上弹钢琴最好的人，来说说我的学习之路",
  "image": "https://pic4.zhimg.com/v2-7bb2fbc0e07fbf66dfc46e3006c9ef8b.jpg",
  "share_url": "http://daily.zhihu.com/story/9472025",
  "js": [],
  "ga_prefix": "061307",
  "images": [
    "https://pic1.zhimg.com/v2-da11a47c396726070ca7009dc2584a04.jpg"
  ],
  "type": 0,
  "id": 9472025,
  "css": [
    "http://news-at.zhihu.com/css/news_qa.auto.css?v=4b3e3"
  ]
}
```

##### 4. 单条消息额外信息

* URL `https://news-at.zhihu.com/api/4/story-extra/9472025`

* 返回值

```json
{
	"vote_status": 0,
	"popularity": 2670,
	"favorite": false,
	"long_comments": 6,
	"comments": 298,
	"short_comments": 292
}
```

