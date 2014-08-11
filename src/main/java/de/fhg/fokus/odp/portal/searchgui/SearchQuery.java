/**
 * Copyright (c) 2012, 2013 Fraunhofer Institute FOKUS
 *
 * This file is part of Open Data Platform.
 *
 * Open Data Platform is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * Open Data Plaform is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with Open Data Platform.  If not, see <http://www.gnu.org/licenses/agpl-3.0>.
 */

package de.fhg.fokus.odp.portal.searchgui;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.portlet.ActionResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.faces.portal.context.LiferayFacesContext;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.model.Layout;
import com.liferay.portal.theme.ThemeDisplay;

import de.fhg.fokus.odp.registry.model.MetadataEnumType;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * The Class SearchQuery.
 */
@ManagedBean
@ViewScoped
public class SearchQuery implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9056912846758434619L;

	/** The Constant log. */
	private static final Logger log = LoggerFactory
			.getLogger(SearchQuery.class);

	/** The query. */
	private String query;
	private String originalQuery;

	/** The metadata type. */
	private String metadataType;

	private String scmBuildnumber;

	private String scmBranch;

	/** The registry client. MSG */
	// @ManagedProperty("#{registryClient}")
	private RegistryClient registryClient;

	/** The registry client. MSG END */

	/**
	 * Inits the.
	 */
	@PostConstruct
	public void init() {
		ThemeDisplay themeDisplay = LiferayFacesContext.getInstance()
				.getThemeDisplay();
		String currentPage = themeDisplay.getLayout().getFriendlyURL();
		if (currentPage.equals("/daten")) {
			metadataType = MetadataEnumType.DATASET.toField();
		} else if (currentPage.equals("/apps")) {
			metadataType = MetadataEnumType.APPLICATION.toField();
		} else if (currentPage.equals("/dokumente")) {
			metadataType = MetadataEnumType.DOCUMENT.toField();
		} else {
			metadataType = "metadata";
		}

		scmBuildnumber = PropsUtil.get("ogdd.buildNumber");
		scmBranch = PropsUtil.get("ogdd.scmBranch");
	}

	/**
	 * Gets the query.
	 * 
	 * @return the query
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * Sets the query.
	 * 
	 * @param query
	 *            the query to set
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	/**
	 * Submit.
	 * 
	 * @return the string
	 */
	public void submit() {
		// Query q = new Query();
		// q.setSearchterm(query);

		ThemeDisplay themeDisplay = LiferayFacesContext.getInstance()
				.getThemeDisplay();
		Layout layout = themeDisplay.getLayout();
		String currentPage = layout.getFriendlyURL();

		FacesContext facesContext = FacesContext.getCurrentInstance();
		Object responseObject = facesContext.getExternalContext().getResponse();
		if (responseObject != null && responseObject instanceof ActionResponse) {
			ActionResponse actionResponse = (ActionResponse) responseObject;
			// actionResponse.setEvent(new
			// QName("http://fokus.fraunhofer.de/odplatform", "querydatasets"),
			// q);

			if (query != null && !query.isEmpty()) {
				log.info("searching for '{}'", query);
				setOriginalQuery(query);
				// String escapedQuery = StringEscapeUtils.escapeHtml(query);
				String escapedQuery = StringUtils.escapeColonString(query);
				// msg 8.5.2014 actionResponse.setRenderParameter("searchterm",
				// query);
				escapedQuery = StringUtils.removeBlankStrings(query);
				log.info("escapedQuery:" + escapedQuery);

				actionResponse.setRenderParameter("searchterm", escapedQuery);
			} else {
				actionResponse.removePublicRenderParameter("searchterm");
			}

			actionResponse.removePublicRenderParameter("searchcategory");
		}

		// if (currentPage.equals("/home")) {
		String location = themeDisplay.getPortalURL();
		if (layout.isPublicLayout()) {
			location += themeDisplay.getPathFriendlyURLPublic();
		}

		try {
			location += layout.hasScopeGroup() ? layout.getScopeGroup()
					.getFriendlyURL() : layout.getGroup().getFriendlyURL();
			if (currentPage.equals("/home")) {
				location += "/suchen";
			} else {
				location += layout.getFriendlyURL();
			}
		} catch (PortalException e) {
			log.error("add group to url", e);
		} catch (SystemException e) {
			log.error("add group to url", e);
		}

		try {
			facesContext.getExternalContext().redirect(location);
		} catch (IOException e) {
			log.error("redirect to result page", e);
		}
		// }

	}

	/**
	 * Gets the metadata type.
	 * 
	 * @return the metadata type
	 */
	public String getMetadataType() {
		return metadataType;
	}

	/**
	 * Sets the metadata type.
	 * 
	 * @param metadataType
	 *            the new metadata type
	 */
	public void setMetadataType(String metadataType) {
		this.metadataType = metadataType;
	}

	/**
	 * @return the scmBuildnumber
	 */
	public String getScmBuildnumber() {
		return scmBuildnumber;
	}

	/**
	 * @param scmBuildnumber
	 *            the scmBuildnumber to set
	 */
	public void setScmBuildnumber(String scmBuildnumber) {
		this.scmBuildnumber = scmBuildnumber;
	}

	/**
	 * @return the scmBranch
	 */
	public String getScmBranch() {
		return scmBranch;
	}

	/**
	 * @param scmBranch
	 *            the scmBranch to set
	 */
	public void setScmBranch(String scmBranch) {
		this.scmBranch = scmBranch;
	}

	/**
	 * Basic Complete TEST
	 */
	public List<String> complete(String fragment) {
		List<String> results = new ArrayList<String>();

		/*
		 * for (int i = 0; i < 10; i++) { results.add(fragment + i); }
		 * 
		 * return results;
		 */

		registryClient = new RegistryClient();
		registryClient.init();
		return results = registryClient.getInstance().autoSuggestMetadata(
				fragment);
	}

	public String getOriginalQuery() {
		log.info("getOriginalQuery:" + originalQuery);
		return originalQuery;
	}

	public void setOriginalQuery(String originalQuery) {
		log.info("setOriginalQuery:" + originalQuery);
		this.originalQuery = originalQuery;
	}

}
