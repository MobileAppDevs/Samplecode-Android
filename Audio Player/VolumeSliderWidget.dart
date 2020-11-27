import 'package:flutter/material.dart';

class VolumeSlider extends StatefulWidget {
  final Function(double) setVolume;

  VolumeSlider(this.setVolume) : super(key: UniqueKey());

  _VolumeSlider createState() => _VolumeSlider();
}

class _VolumeSlider extends State<VolumeSlider> {
  double volume = 0.5;

  @override
  Widget build(BuildContext context) {
    return Container(
      // margin: EdgeInsets.all(10.0),
      width: 250,
      child: Slider(
        // activeColor: Colors.yellow,
        onChanged: (val) {
          widget.setVolume(val);
          setState(() {
            volume = val;
          });
        },
        value: volume,
        max: 1.0,
        min: 0.0,
      ),
    );
  }
}
