package mods.ir.data.campaign;

import java.awt.Color;
import java.util.EnumSet;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignEngineLayers;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.abilities.BaseToggleAbility;
import com.fs.starfarer.api.impl.campaign.abilities.GraviticScanData;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.terrain.SlipstreamTerrainPlugin.Stream;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import mods.common.MyMisc;
import mods.ir.data.hullmods.HE_ImprovisedRefinery;

/**
 * Does nothing on it's own, but state of this ability is used to control
 * ImprovisedRefinery hullmod for player
 */
public class HE_AbilityToggle extends BaseToggleAbility {
	public static final String ID = "HE_AbilityToggle";

	@Override
	protected void applyEffect(float arg0, float arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void cleanupImpl() {
		// TODO Auto-generated method stub
	}

	@Override
	protected String getActivationText() {
		return "Ore Refinery activated";
	}

	@Override
	protected String getDeactivationText() {
		return null;
	}

	@Override
	protected void activateImpl() {
	}

	@Override
	protected void deactivateImpl() {
	}

	@Override
	public boolean showActiveIndicator() {
		return isActive();
	}

	@Override
	public void createTooltip(TooltipMakerAPI tooltip, boolean expanded) {
		Color bad = Misc.getNegativeHighlightColor();
		Color gray = Misc.getGrayColor();
		Color highlight = Misc.getHighlightColor();

		String status = " (off)";
		if (turnedOn) {
			status = " (on)";
		}

		LabelAPI title = tooltip.addTitle(spec.getName() + status);
		title.highlightLast(status);
		title.setHighlightColor(gray);

		float pad = 10f;

		CampaignFleetAPI fleet = getFleet();
		List<FleetMemberAPI> fleetMemebers = fleet.getFleetData().getMembersListCopy();

		int oreProcessedPerDay = 0;
		for (FleetMemberAPI member : fleetMemebers) {
			oreProcessedPerDay += member.getVariant().hasHullMod(HE_ImprovisedRefinery.ID)
					? HE_ImprovisedRefinery.getConversionRatePerDay(member,
							Global.getSettings().getHullModSpec(HE_ImprovisedRefinery.ID))
					: 0;
		}
		;

		tooltip.addPara("Toggles Ore Refinery hullmod for all ships.",
				pad);

		tooltip.addPara(
				"Your refineries are capable of converting %s Ore into %s Metals per day.",
				pad, highlight,
				"" + (int) oreProcessedPerDay,
				"" + ((HE_ImprovisedRefinery) MyMisc.getHullMod(HE_ImprovisedRefinery.ID)).getRecievedMetals(
						fleet.getFleetData().getFleet().getFlagship(),
						oreProcessedPerDay));

		// tooltip.addPara("Increases the range at which the fleet can be detected by
		// %s.",
		// pad, highlight,
		// "" + (int) DETECTABILITY_PERCENT + "%");

		// if (getFleet() != null && getFleet().isInHyperspace()) {
		// tooltip.addPara("Can not function in hyperspace.", bad, pad);
		// } else {
		// tooltip.addPara("Can not function in hyperspace.", pad);
		// }

		// tooltip.addPara("Disables the transponder when activated.", pad);
		addIncompatibleToTooltip(tooltip, expanded);
	}

	public boolean hasTooltip() {
		return true;
	}

	@Override
	public EnumSet<CampaignEngineLayers> getActiveLayers() {
		return EnumSet.of(CampaignEngineLayers.ABOVE);
	}

	// @Override
	// public void advance(float amount) {
	// super.advance(amount);

	// if (data != null && !isActive() && getProgressFraction() <= 0f) {
	// data = null;
	// }
	// }

	@Override
	public boolean isUsable() {
		CampaignFleetAPI fleet = getFleet();
		if (fleet == null)
			return false;

		return !Misc.isInsideSlipstream(fleet);
		// return isActive() || !fleet.isInHyperspace();
	}

}
