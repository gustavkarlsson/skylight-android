package se.gustavkarlsson.aurora_notifier.android.background.providers.impl;

import com.google.gson.annotations.SerializedName;

class OpenWeatherMapWeather {

	@SerializedName("clouds")
	private Clouds clouds;

	Clouds getClouds() {
		return clouds;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		OpenWeatherMapWeather that = (OpenWeatherMapWeather) o;

		return clouds != null ? clouds.equals(that.clouds) : that.clouds == null;

	}

	@Override
	public int hashCode() {
		return clouds != null ? clouds.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "OpenWeatherMapWeather{" +
				"clouds='" + clouds + '\'' +
				'}';
	}

	static class Clouds {

		@SerializedName("all")
		private Integer all;

		int getAll() {
			return all;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Clouds that = (Clouds) o;

			return all != null ? all.equals(that.all) : that.all == null;
		}

		@Override
		public int hashCode() {
			return all != null ? all.hashCode() : 0;
		}

		@Override
		public String toString() {
			return "Clouds{" +
					"all=" + all +
					'}';
		}
	}
}
