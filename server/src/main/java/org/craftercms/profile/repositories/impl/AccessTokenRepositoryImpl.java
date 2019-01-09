/*
 * Copyright (C) 2007-2019 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.profile.repositories.impl;

import org.craftercms.commons.mongo.AbstractJongoRepository;
import org.craftercms.profile.api.AccessToken;
import org.craftercms.profile.repositories.AccessTokenRepository;

/**
 * Default implementation of {@link org.craftercms.profile.repositories.AccessTokenRepository}, using Jongo.
 *
 * @author avasquez
 */
public class AccessTokenRepositoryImpl extends AbstractJongoRepository<AccessToken> implements AccessTokenRepository {

}
