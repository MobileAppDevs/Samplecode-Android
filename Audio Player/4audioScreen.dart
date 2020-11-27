import 'package:assets_audio_player/assets_audio_player.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:audioplayers/audioplayers.dart';
import 'package:flutter_sophie_music/AudioUrl.dart';
import 'package:flutter_sophie_music/Ui/AudioPlayer.dart';
import 'package:flutter_sophie_music/Ui/HomeScreen.dart';
import 'package:flutter_sophie_music/Ui/StoryScreen.dart';
import 'package:flutter_sophie_music/Ui/VolumeSliderWidget.dart';
import 'package:audioplayers/audio_cache.dart';
import 'package:flutter_sophie_music/utils/hexColor.dart';

enum PlayerState { stopped, playing, paused }

class AudioNewScreen extends StatefulWidget {
  final bool isVisible;

  const AudioNewScreen({Key key, this.isVisible}) : super(key: key);

  @override
  _ForestScreen createState() => _ForestScreen(this.isVisible);
}

class _ForestScreen extends State<AudioNewScreen> {
  PlayerState _playerState = PlayerState.stopped;
  final bool isVisible;

  _ForestScreen(this.isVisible);

  get _isPlaying => _playerState == PlayerState.playing;

  AssetsAudioPlayer assetsAudioPlayer1;
  AssetsAudioPlayer assetsAudioPlayer2;
  AssetsAudioPlayer assetsAudioPlayer3;
  AssetsAudioPlayer assetsAudioPlayer4;

  double slider1 = 0.5;
  double slider2 = 0.5;
  double slider3 = 0.5;
  double slider4 = 0.5;

  @override
  void initState() {
    super.initState();
    assetsAudioPlayer1 = AssetsAudioPlayer();
    assetsAudioPlayer2 = AssetsAudioPlayer();
    assetsAudioPlayer3 = AssetsAudioPlayer();
    assetsAudioPlayer4 = AssetsAudioPlayer();
    assetsAudioPlayer1.setVolume(0.5);
    assetsAudioPlayer2.setVolume(0.5);
    assetsAudioPlayer3.setVolume(0.5);
    assetsAudioPlayer4.setVolume(0.5);
  }

  @override
  void dispose() {
    super.dispose();
  }

  void _play() {
    assetsAudioPlayer1.open(Audio("assets/Broken_Village.mp3"),
        loopMode: LoopMode.single);
    assetsAudioPlayer2.open(Audio("assets/Elven_Forest.mp3"),
        loopMode: LoopMode.single);
    assetsAudioPlayer3.open(Audio("assets/The_Dark_Castle.mp3"),
        loopMode: LoopMode.single);

    assetsAudioPlayer4.open(Audio("assets/Relaxing_Green_Nature.mp3"),
        loopMode: LoopMode.single);

    setState(() {
      _playerState = PlayerState.playing;

      assetsAudioPlayer1.play();
      assetsAudioPlayer2.play();
      assetsAudioPlayer3.play();
      assetsAudioPlayer4.play();
    });
  }

  void _pause() async {
    final result = await Future.wait([
      assetsAudioPlayer4.pause(),
      assetsAudioPlayer3.pause(),
      assetsAudioPlayer2.pause(),
      assetsAudioPlayer1.pause()
    ]);
    setState(() {
      _playerState = PlayerState.paused;

      assetsAudioPlayer1.pause();
      assetsAudioPlayer2.pause();
      assetsAudioPlayer3.pause();
      assetsAudioPlayer4.pause();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
          height: MediaQuery.of(context).size.height,
          decoration: BoxDecoration(
              image: DecorationImage(
                  fit: BoxFit.fill,
                  image: AssetImage(
                    "assets/images/main_bg.png",
                  ))),
          child: new SingleChildScrollView(
              child: new Container(
                  child: new Column(children: <Widget>[
            Stack(
              children: <Widget>[
                Center(
                    child: Column(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: <Widget>[
                    //first element in the column is the white background (the Image.asset in your case)
                    Container(
                      // height: 200,
                      // width: 200,
                      // height: MediaQuery.of(context).size.height/1.3,
                      width: double.infinity,
                      margin: const EdgeInsets.only(
                          left: 10.0, right: 10.0, top: 70),
                      padding: EdgeInsets.only(left: 10, top: 20),
                      decoration: new BoxDecoration(
                          color: HexColor("#EAEFF3").withOpacity(0.5),
                          //here i want to add opacity
                          border: new Border.all(color: Colors.green, width: 4),
                          borderRadius: new BorderRadius.only(
                              topLeft: const Radius.circular(15.0),
                              topRight: const Radius.circular(15.0),
                              bottomLeft: const Radius.circular(15.0),
                              bottomRight: const Radius.circular(15.0))),

                      child: Row(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: <Widget>[
                          Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              Padding(padding: EdgeInsets.only(top: 15)),
                              Row(
                                mainAxisAlignment:
                                    MainAxisAlignment.spaceBetween,
                                children: [
                                  Container(
                                      child: Image.asset(
                                    'assets/images/player_forest.png',
                                    // width: 70.0,
                                    // height: 70.0,
                                    fit: BoxFit.fill,
                                  )),
                                  SliderTheme(
                                      data: SliderTheme.of(context).copyWith(
                                        // valueIndicatorColor: Colors.blue, // This is what you are asking for
                                        inactiveTrackColor: Color(0xFF8D8E98),
                                        // Custom Gray Color
                                        activeTrackColor: Colors.green,
                                        thumbColor: Colors.green,
                                        // overlayColor: Color(0x29EB1555),  // Custom Thumb overlay Color
                                        thumbShape: RoundSliderThumbShape(
                                            enabledThumbRadius: 6.0),
                                        overlayShape: RoundSliderOverlayShape(
                                            overlayRadius: 2.0),
                                        trackHeight: 2,
                                      ),
                                      child: VolumeSlider((volume) {
                                        assetsAudioPlayer1.setVolume(volume);
                                      }))
                                ],
                              ),

                              Padding(padding: EdgeInsets.only(top: 25)),
                              Row(
                                children: [
                                  Container(
                                      child: Image.asset(
                                    'assets/images/player_sea.png',
                                    // width: 70.0,
                                    // height: 70.0,
                                    fit: BoxFit.fill,
                                    // ),
                                  )),
                                  SliderTheme(
                                      data: SliderTheme.of(context).copyWith(
                                        // valueIndicatorColor: Colors.blue, // This is what you are asking for
                                        inactiveTrackColor: Color(0xFF8D8E98),
                                        // Custom Gray Color
                                        activeTrackColor: Colors.blue,
                                        thumbColor: Colors.blue,
                                        thumbShape: RoundSliderThumbShape(
                                            enabledThumbRadius: 6.0),
                                        overlayShape: RoundSliderOverlayShape(
                                            overlayRadius: 2.0),
                                        trackHeight: 2,
                                        // overlayColor: Color(0x29EB1555),  // Custom Thumb overlay Color
                                        // thumbShape:
                                        // RoundSliderThumbShape(enabledThumbRadius: 12.0),
                                        // overlayShape:
                                        // RoundSliderOverlayShape(overlayRadius: 20.0),
                                      ),
                                      child: VolumeSlider((volume) {
                                        assetsAudioPlayer2.setVolume(volume);
                                      }))
                                ],
                              ),

                              Padding(padding: EdgeInsets.only(top: 25)),
                              Row(
                                children: [
                                  Container(
                                      child: Image.asset(
                                    'assets/images/player_cave.png',
                                    // width: 70.0,
                                    // height: 70.0,
                                    fit: BoxFit.fill,
                                  )),
                                  SliderTheme(
                                      data: SliderTheme.of(context).copyWith(
                                        // valueIndicatorColor: Colors.blue, // This is what you are asking for
                                        inactiveTrackColor: Color(0xFF8D8E98),
                                        // Custom Gray Color
                                        activeTrackColor: Colors.brown,
                                        thumbColor: Colors.brown,
                                        thumbShape: RoundSliderThumbShape(
                                            enabledThumbRadius: 6.0),
                                        overlayShape: RoundSliderOverlayShape(
                                            overlayRadius: 2.0),
                                        trackHeight: 2,
                                      ),
                                      child: VolumeSlider((volume) {
                                        assetsAudioPlayer3.setVolume(volume);
                                      }))
                                ],
                              ),

                              Padding(padding: EdgeInsets.only(top: 25)),
                              Row(
                                children: [
                                  Container(
                                      child: Image.asset(
                                    'assets/images/player_mountain.png',
                                    // width: 70.0,
                                    // height: 70.0,
                                    fit: BoxFit.fill,
                                  )),
                                  SliderTheme(
                                      data: SliderTheme.of(context).copyWith(
                                        // valueIndicatorColor: Colors.blue, // This is what you are asking for
                                        inactiveTrackColor: Color(0xFF8D8E98),
                                        // Custom Gray Color
                                        activeTrackColor: Colors.brown[800],
                                        thumbColor: Colors.brown[800],
                                        thumbShape: RoundSliderThumbShape(
                                            enabledThumbRadius: 6.0),
                                        overlayShape: RoundSliderOverlayShape(
                                            overlayRadius: 2.0),
                                        trackHeight: 2,
                                      ),
                                      child: VolumeSlider((volume) {
                                        assetsAudioPlayer4.setVolume(volume);
                                      }))
                                ],
                              ),
                              // Padding( padding: EdgeInsets.only(top: 25, ),
                              Padding(
                                padding: EdgeInsets.only(top: 80, bottom: 10),
                                child: Visibility(
                                  visible: isVisible,
                                  child: Align(
                                      // mainAxisAlignment: MainAxisAlignment.center,
                                      // crossAxisAlignment: CrossAxisAlignment.center,
                                      // children: [
                                      alignment: Alignment.center,
                                      child: Column(
                                        crossAxisAlignment:
                                            CrossAxisAlignment.center,
                                        mainAxisAlignment:
                                            MainAxisAlignment.center,
                                        children: [
                                          Container(
                                              // color: Colors.yellow,
                                              width: MediaQuery.of(context)
                                                      .size
                                                      .width /
                                                  1.2,
                                              child: Text(
                                                'IF YOU LIKE, YOU CAN LISTEN TO \n THE STORY AGAIN.',
                                                textAlign: TextAlign.center,
                                                maxLines: 2,
                                                style: TextStyle(
                                                    fontSize: 15,
                                                    color: Colors.black,
                                                    // fontWeight: FontWeight.w500,
                                                    //   fontStyle: FontStyle.italic
                                                    fontFamily: "COINY"),
                                              ))
                                        ],
                                      )

                                      // ],

                                      ),
                                ),
                              ),
                            ],
                          )
                        ],
                      ),
                    ),

                    Padding(
                        padding: EdgeInsets.only(left: 50, right: 50),
                        child: Stack(children: [
                          // Container(
                          //
                          Center(
                              child: Image.asset(
                            "assets/images/player_rasi.png",
                            // fit: BoxFit.fill,
                            fit: BoxFit.cover,
                            // height: MediaQuery.of(context).size.height,
                            alignment: Alignment.center,
                            // ),
                          )),
                          Padding(
                              padding: EdgeInsets.only(top: 35),
                              child: Column(children: [
                                Align(
                                    alignment: Alignment.center,
                                    child: GestureDetector(
                                      onTap: () {
                                        _pause();
                                        Navigator.push(
                                            context,
                                            MaterialPageRoute(
                                                builder: (context) =>
                                                    StoryScreen(
                                                        isVisible: false)));
                                      },
                                      child: Padding(
                                          padding: EdgeInsets.only(left: 0),
                                          child: Stack(
                                            children: [
                                              Image.asset(
                                                  'assets/images/startt_story.png'),
                                            ],
                                          )),
                                    )),
                                Visibility(
                                    visible: isVisible,
                                    child: Padding(
                                        padding: EdgeInsets.only(top: 30),
                                        child: Align(
                                            alignment: Alignment.center,
                                            child: GestureDetector(
                                              onTap: () {
                                                _pause();
                                                Navigator.push(
                                                    context,
                                                    MaterialPageRoute(
                                                        builder: (context) =>
                                                            HomeScreen()));
                                              },
                                              child: Padding(
                                                  padding:
                                                      EdgeInsets.only(left: 0),
                                                  child: Stack(
                                                    children: [
                                                      Image.asset(
                                                          'assets/images/home_btn.png'),
                                                    ],
                                                  )),
                                            )))),
                              ])),
                        ])),

                    Container(
                      height: 20.0,
                      // alignment: Alignment.bottomRight,
                    )
                  ],
                )),
                Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: <Widget>[
                      //first element in column is the transparent offset
                      Container(
                        height: 470.0,

                        // alignment: Alignment.bottomRight,
                      ),
                      Padding(
                        padding: EdgeInsets.only(top: 0),
                        child: Align(
                            alignment: Alignment.bottomCenter,
                            child: FloatingActionButton(
                                onPressed: () {
                                  _isPlaying ? _pause() : _play();
                                  // Navigator.push(context,
                                  //     MaterialPageRoute(builder: (context) => StoryScreen(isVisible: false)));},
                                  //
                                },
                                // child: Image.asset('assets/images/play_icon.png'),
                                child: _isPlaying
                                    ? new Image.asset(
                                        'assets/images/pause_icon.png')
                                    : new Image.asset(
                                        'assets/images/play_icon.png'))),
                      ),
                    ]),
                //for the button i create another column
              ],
            ),
          ])))),
    );
  }
}
