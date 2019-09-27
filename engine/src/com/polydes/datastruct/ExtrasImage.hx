package com.polydes.datastruct;

import com.stencyl.Engine;
import openfl.Assets;
import openfl.utils.AssetType;
import openfl.display.BitmapData;
import openfl.geom.Matrix;

abstract ExtrasImage(BitmapData) from BitmapData to BitmapData
{
	inline public function new(bmp:BitmapData)
	{
		this = bmp;
	}

	public static function fromString(s:String):ExtrasImage
	{
		if(s == "")
			return null;
		s = s.substring(0, s.lastIndexOf(".")); // strip .png from the end
		if(Engine.IMG_BASE == "1x")
			return Assets.getBitmapData("assets/data/"+ s +".png");
		else
		{
			if(Assets.exists("assets/data/"+ s + "@" + Engine.IMG_BASE + ".png", AssetType.IMAGE))
			return Assets.getBitmapData("assets/data/"+ s + "@" + Engine.IMG_BASE + ".png");
			
			return scaleBitmap(Assets.getBitmapData("assets/data/"+ s +".png"), Engine.SCALE);
		}
	}

	public static function scaleBitmap(src:BitmapData, s:Float):BitmapData
	{
		var newImg:BitmapData = new BitmapData(Std.int(src.width * s), Std.int(src.height * s), true, 0);
		var matrix:Matrix = new Matrix();
		matrix.scale(s, s);
		newImg.draw(src, matrix);
		return newImg;
	}
}